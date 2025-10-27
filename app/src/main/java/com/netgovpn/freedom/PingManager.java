// PingManager.java
package com.netgovpn.freedom;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class PingManager {

    public interface PingFunction {

        int ping(@NonNull String serverConfig) throws Exception;
    }

    public static class PingResult {
        @NonNull public final String serverConfig;
        @Nullable public final Integer delayMs;
        @Nullable public final Exception error;

        public PingResult(@NonNull String serverConfig, @Nullable Integer delayMs, @Nullable Exception error) {
            this.serverConfig = serverConfig;
            this.delayMs = delayMs;
            this.error = error;
        }
    }

    public interface PingCallback {

        void onFirstSuccess(@NonNull PingResult result);


        void onAllFailed(@NonNull List<PingResult> results);


        default void onCancelled() {}
    }

    private final int maxConcurrency;
    private final long perServerTimeoutMs;
    private final long overallTimeoutMs;

    private ExecutorService executor;
    private ScheduledExecutorService canceller;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final AtomicBoolean isRunning = new AtomicBoolean(false);


    public PingManager(int maxConcurrency, long perServerTimeoutMs, long overallTimeoutMs) {
        if (maxConcurrency <= 0) throw new IllegalArgumentException("maxConcurrency must be > 0");
        this.maxConcurrency = maxConcurrency;
        this.perServerTimeoutMs = perServerTimeoutMs;
        this.overallTimeoutMs = overallTimeoutMs;
    }


    public void startPing(@NonNull List<String> serverConfigs,
                          @NonNull PingFunction pingFunction,
                          @NonNull PingCallback callback) {

        if (serverConfigs.isEmpty()) {
            mainHandler.post(() -> callback.onAllFailed(Collections.emptyList()));
            return;
        }

        if (!isRunning.compareAndSet(false, true)) {
            mainHandler.post(callback::onCancelled);
            return;
        }

        executor = Executors.newFixedThreadPool(Math.min(maxConcurrency, serverConfigs.size()));
        canceller = Executors.newScheduledThreadPool(1);

        final CompletionService<PingResult> completion = new ExecutorCompletionService<>(executor);
        final List<Future<PingResult>> futures = new ArrayList<>(serverConfigs.size());
        final List<PingResult> allResults = Collections.synchronizedList(new ArrayList<>());
        final AtomicBoolean found = new AtomicBoolean(false);

        final long overallDeadline = System.currentTimeMillis() + overallTimeoutMs;

        // submit tasks
        for (String cfg : serverConfigs) {
            Future<PingResult> future = completion.submit(() -> {
                try {
                    int delay = pingFunction.ping(cfg);
                    if (delay < 0) {
                        return new PingResult(cfg, null, new Exception("negative delay returned"));
                    } else {
                        return new PingResult(cfg, delay, null);
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return new PingResult(cfg, null, ie);
                } catch (Exception e) {
                    return new PingResult(cfg, null, e);
                }
            });

            canceller.schedule(() -> {
                if (!future.isDone()) {
                    future.cancel(true);
                }
            }, perServerTimeoutMs, TimeUnit.MILLISECONDS);

            futures.add(future);
        }

        executor.execute(() -> {
            int received = 0;
            final int n = serverConfigs.size();

            try {
                while (received < n && !found.get()) {
                    long now = System.currentTimeMillis();
                    long remainingOverall = overallDeadline - now;
                    if (remainingOverall <= 0) {
                        // overall timeout reached
                        break;
                    }

                    Future<PingResult> completedFuture = completion.poll(Math.min(remainingOverall, perServerTimeoutMs), TimeUnit.MILLISECONDS);
                    if (completedFuture == null) {
                        continue;
                    }

                    PingResult res;
                    try {
                        res = completedFuture.get();
                    } catch (CancellationException ce) {

                        received++;
                        continue;
                    } catch (ExecutionException ee) {
                        Throwable cause = ee.getCause();
                        res = new PingResult("unknown", null, (cause instanceof Exception) ? (Exception) cause : new Exception(cause));
                    }

                    received++;
                    allResults.add(res);

                    if (res.delayMs != null && !found.get() && found.compareAndSet(false, true)) {
                        for (Future<PingResult> f : futures) {
                            if (f != completedFuture && !f.isDone()) {
                                f.cancel(true);
                            }
                        }
                        // shutdown executors
                        shutdownExecutors();

                        PingResult finalRes = res;
                        mainHandler.post(() -> {
                            callback.onFirstSuccess(finalRes);
                        });
                        return;
                    }
                }

                shutdownExecutors();

                for (Future<PingResult> f : futures) {
                    if (!f.isDone()) {
                        f.cancel(true);
                    } else {
                        try {
                            PingResult r = f.get(10, TimeUnit.MILLISECONDS);
                            if (!allResults.contains(r)) allResults.add(r);
                        } catch (Exception ignore) {
                            // ignore
                        }
                    }
                }

                mainHandler.post(() -> {
                    callback.onAllFailed(Collections.unmodifiableList(new ArrayList<>(allResults)));
                });

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                isRunning.set(false);
            }
        });
    }


    public void stop() {
        if (!isRunning.get()) return;
        isRunning.set(false);
        shutdownExecutors();
    }

    private void shutdownExecutors() {
        if (canceller != null && !canceller.isShutdown()) {
            canceller.shutdownNow();
            canceller = null;
        }
        if (executor != null && !executor.isShutdown()) {
            executor.shutdownNow();
            executor = null;
        }
    }
}
