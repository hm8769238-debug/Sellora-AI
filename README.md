# Sellora AI — Smart Sales Assistant for Online Businesses

**Sellora AI** is a professional AI-powered commerce assistant built for small and medium online business owners across Instagram, WhatsApp, Facebook Marketplace, and independent boutiques.

---

## 🌟 Target Customers
1. Instagram Clothing Store Owners
2. Online Boutique Owners
3. Jewellery Business Owners
4. Cosmetics & Skincare Sellers
5. Saree & Ethnic Wear Sellers
6. Handmade Product Sellers
7. Shoes & Footwear Sellers
8. Bags & Accessories Sellers
9. Home Decor Business Owners
10. Small E-commerce Business Owners
11. Facebook Marketplace Sellers
12. Instagram Resellers
13. Small D2C Brand Owners
14. WhatsApp Business Sellers
15. Online Gift Shop Owners

---

## 🚀 Key Modules & Capabilities

### 1. AI Sales Reply Generator (Core Engine)
- Generates 5 distinct conversion-tested response variations:
  - **Ready-to-Send Customer Reply**
  - **Short Reply**
  - **Friendly Reply**
  - **Professional Reply**
  - **Premium-Brand Concierge Reply**
  - **Suggested Closing Follow-Up Question**
- Supports **English**, **Hindi (हिंदी)**, and **Bengali (বাংলা)**.
- **Strict Grounding Rule**: Strictly answers using actual saved catalog specs, prices, colors, and shipping policies. Never hallucinates discounts or promises unverified stock.
- One-tap copy to clipboard and one-tap "Save as Customer Lead".

### 2. 14 Specialized AI Commerce Tools
1. **Product Description Generator**
2. **Instagram Caption Generator** (Hook + Story + CTA)
3. **Social Media Hashtag Suggestions** (Reach, Niche, Intent)
4. **Festival Offer Message Generator** (Diwali, Eid, Puja, New Year)
5. **Customer FAQ Generator**
6. **Shipping and Return Reply Generator**
7. **Abandoned Inquiry Follow-up Draft Generator**
8. **Customer Objection Response Generator** (Overcoming price & trust doubts)
9. **Upselling and Cross-selling Suggestions**
10. **Product Comparison Reply Generator**
11. **Promotional Email Draft Generator**
12. **WhatsApp Message Draft Generator**
13. **Customer Review Reply Generator** (5-star gratitude & 1-star resolution)
14. **Frequently Asked Questions Manager**

### 3. Product Catalog
- Offline-first local database using Android Room with zero cloud vendor lock-in.
- Full product fields: Name, Category, SKU, Price, Currency, Available Sizes, Colors, Stock Status, Delivery Details, FAQs, Description.
- Search and stock status filtering.
- Full **CSV Import & Export** support.
- Downloadable/copyable sample CSV template.

### 4. Customer Lead Manager
- Capture buyer inquiries manually from WhatsApp, DM, or phone.
- Track lead status stages: `New`, `Interested`, `Follow-up Needed`, `Converted`, `Lost`.
- Next follow-up reminder scheduling.
- "Draft AI Reply" action linking inquiries directly to the AI Reply Generator.
- CSV export for CRM backups.

### 5. Quick Reply Templates
- 12 pre-loaded commerce templates with dynamic variable interpolation (`{business_name}`, `{product_name}`, `{price}`, `{colors}`, `{sizes}`, `{contact}`).
- Add, edit, copy, and delete custom store templates.

### 6. Business Setup Wizard & Settings
- Comprehensive setup wizard guiding store owners through their business profile, policies, currency, and brand tone.
- Fully editable anytime.

---

## 🛠️ Setup & Running

### Mobile (Android)
- Minimum SDK: Android 7.0 (API 24)
- Target SDK: Android 15 / 16 (API 36)
- Build:
  ```bash
  gradle :app:assembleDebug
  ```

### API Configuration
To enable live Gemini AI generation:
1. Open the **Secrets panel** in Google AI Studio or add to `.env`:
   ```env
   GEMINI_API_KEY=your_actual_gemini_api_key_here
   ```
2. If no key is provided, Sellora AI runs in **Demo Mode**, using catalog data to generate answers locally.

---

## 📄 Licensing & Commercial Usage
- Clean commercial architecture ready for digital product sale to merchant clients.
