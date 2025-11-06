# NetGoVPN — Android Client

<p align="center">
  <img src="docs/cover.png" alt="Project Cover" width="600"/>
</p>

**Tagline:** *Internet freedom for everyone — resilient, private, and open.*

NetGoVPN is a free, open-source Android client engineered to provide reliable, privacy-respecting, and censorship-resistant network connectivity for users in high-restriction environments. The project combines well-established open-source networking components with custom resilience features and an emphasis on transparency, auditability, and user safety.

---

## 🚩 Mission

NetGoVPN exists to preserve and extend access to open information: to empower people living under severe network restrictions with tools that increase their chances of reaching uncensored resources while minimizing risk to users. We prioritize safety, privacy, and transparency.

---

## 🛡️ Censorship-Resistant Capabilities

NetGoVPN is designed with real-world censorship and deep packet inspection (DPI) in mind. Important behaviors and design goals:

* **Automated fallback and multi-protocol support**: the client supports multiple transports and can switch when primary paths are blocked.
* **Obfuscation and camouflage strategies**: techniques to make traffic less fingerprintable (implementation details are intentionally high-level in public docs to protect users and infrastructure).
* **Dynamic server rotation & failover**: automatic selection of healthy endpoints to maintain connectivity.
* **Minimal surface for detection**: lightweight client behavior and conservative telemetry.

> Note: We do **not** publish operational details that would materially assist third parties in exploiting anti-censorship techniques. This is a deliberate choice to protect user safety and infrastructure.

---

## ✨ Key Features

* Lightweight, stable client compatible with Android API 23+ (tested up to Android 15)
* Optimized background services for modern Android lifecycle and power management
* Advanced routing logic and connection-quality monitoring
* Privacy-respecting analytics (no personal identifiers collected)
* Modular architecture to integrate additional transports and resistances
* Simple UX designed for non-technical users in risky environments

---

## 🔒 Privacy & Security

* **No-logs policy:** We do not store users' activity or traffic logs on servers. Operational metadata required for infrastructure health (e.g., heartbeat or ephemeral connection counters) is minimized and retained only for short periods.
* **Minimal telemetry:** Only non-identifying performance metrics are collected to help maintain stability and reliability.
* **Responsible disclosure:** See [SECURITY.md](SECURITY.md) for vulnerability reporting and PGP key for confidential reports.
* **Code separation:** Security-sensitive server-side bits are not published in this client repo to avoid exposing operational infrastructure.

---

## 🧭 Threat Model (summary)

**Primary threats considered:** network-level blocking and surveillance, DPI, service disruption, accidental deanonymization.

**We do not assume:** that the project will protect users who intentionally break local laws or engage in targeted malicious activities. Users in high-risk situations should consult local guidance and be aware of legal risks.

---

## 🛠️ Tech Stack & Third-Party Components

NetGoVPN leverages established open-source networking projects as building blocks and adds project-specific resilience and UX layers.

* **Core protocols / engines:** Xray, V2Ray (used as transport/protocol engines)
* **Management UI:** X-UI components adapted for mobile management and user experience
* **Client:** Native Android (Java/Kotlin), modular structure
* **Telemetry & analytics:** Firebase (opt-in) + self-hosted aggregated metrics

**Important:** We document exactly which parts are upstream (Xray/V2Ray/X-UI) and which parts are our original code. Reuse of permissively-licensed upstream components is standard practice and supported by the open-source community.

---

## 📣 What Makes NetGoVPN Different

* Focus on **real-world censorship resistance** for users in severely restricted regions
* Lightweight UX tailored for non-technical users and low-bandwidth conditions
* Transparent, auditable client with a short list of minimal trust requirements
* Active monitoring and automated fallback for robust availability

---

## 📦 Build & Deployment

1. Clone this repository and open in Android Studio Flamingo or newer.
2. Follow build instructions in [BUILD.md](BUILD.md) (sensitive infrastructure details are intentionally excluded from the public repo).
3. Releases are signed and published to GitHub Releases and Google Play (managed channel). See release notes for binary hashes.

---

## 🧾 Roadmap (6 months — grant-focused)

**Month 1 — Launch & Baseline Metrics**

* Google Play release, initial user feedback collection, 20+ positive reviews
* Public `funding.json` + one-pager for grant applications

**Month 2 — Security Hardening**

* Internal audits, privacy policy finalized, add basic automated anti-regression tests

**Month 3 — Growth & Onboarding**

* Targeted onboarding improvements, referral mechanisms, community outreach

**Month 4 — Multi-region Scaling**

* Add additional server regions, automated failover, capacity testing

**Month 5 — Transparency & Trust**

* Publish impact dashboard, expand SECURITY.md, collect community endorsements

**Month 6 — Grant Application Ready**

* Reach target MAU (10k), collect 2–3 support letters, submit 25k grant proposal

---

## 📈 Metrics & Impact (what we'll show to funders)

* Monthly Active Users (MAU)
* Daily successful connections
* Average connection time
* Geographic distribution of users (high-level, aggregated — no PII)
* Retention and churn rates

---

## 💸 Funding & Sustainability

NetGoVPN is free for end-users. Funding will be used for: server capacity, development, security audits, and operational costs. Funding channels we will pursue:

* Grants (-)
* Donations (crypto & GitHub Sponsors)
* Sponsorships from privacy-forward organizations

---

## 🔗 Donations & Support

If you want to support the project:

* Crypto: [DONATE.md](DONATE.md)

---

## ⚖️ Legal & Compliance

NetGoVPN is designed to help users access information and communications. It is **not** intended to facilitate unlawful activity. Maintainers and contributors must comply with applicable laws, export controls, and platform terms of service (including GitHub and Google Play policies).

* **Do not** provide instructions in this public repository that explicitly teach how to evade sanctions or assist in committing crimes. Operational guidance that meaningfully facilitates evasion of sanctions or illegal acts is intentionally omitted.
* For questions about receiving international funding or payments, consult legal counsel or a fiscal sponsor.

---

## 📚 Contributing

Contributions are welcome. Please read [CONTRIBUTING.md](CONTRIBUTING.md) for the code of conduct, contribution workflow, and security reporting process.

---

## 📞 Contact

* Website: [https://netgovpn.com](https://netgovpn.com)
* Support: [support@netgovpn.com](mailto:support@netgovpn.com)
* Telegram: [https://t.me/NetGoVPN_Team](https://t.me/NetGoVPN_Team)

---

## 📄 License

This repository is licensed under the MIT License. See `LICENSE` for details.

---

**Prepared for grant reviewers:** includes clear mission, impact metrics, and privacy-first approach. Funding-related artifacts (one-pager, `funding.json`, budget spreadsheets, and support letters) live in `docs/grants/` and are updated before submission.
