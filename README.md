# Kurumsal Envanter Yönetim Sistemi

**Geliştirici:** Cem Emre Demir[cite: 1]
**Kurum:** Işık Üniversitesi[cite: 1]

## 📌 Proje Özeti
Bu proje, bir kuruluşun teknolojik varlıklarını ve personel zimmet süreçlerini uçtan uca yönetmek amacıyla geliştirilmiş bir Kurumsal Envanter Yönetim Sistemidir[cite: 1]. Sistem; manuel envanter takibinde oluşabilecek veri kayıplarını önlemeyi, stok yönetimini dijital ortama taşımayı ve idari birimler ile personel arasındaki zimmet süreçlerini güvenli bir web platformu üzerinden yönetmeyi hedefler[cite: 1].

## 🚀 Temel Özellikler
* **Güvenlik ve Rol Tabanlı Erişim (RBAC):** Sistem güvenliği Spring Security altyapısı kullanılarak sağlanmış olup, Yönetici (Admin) ve Personel (User) olmak üzere iki temel yetki seviyesinden oluşmaktadır[cite: 1].
* **Gelişmiş Yönetici (Admin) Paneli:** Zimmet taleplerinin onay/red işlemleri, personel bilgileri yönetimi, ürün girişleri, kritik stok takibi ve tedarikçi yönetimi bu panel üzerinden gerçekleştirilmektedir[cite: 1].
* **Kişiselleştirilmiş Personel Paneli:** Kullanıcıların kendilerine zimmetli cihazları görüntülemesine, yeni donanım zimmet talebi oluşturmasına ve iade süreçlerini dijital ortamda başlatmasına olanak sağlar[cite: 1].
* **Otomatik Stok Senkronizasyonu:** Bir zimmet talebi onaylandığında ürün stoğu otomatik olarak düşürülerek veritabanı seviyesinde veri tutarlılığı (Transactional) korunmaktadır[cite: 1].
* **Merkezi Günlükleme ve Denetim (Audit Logging):** Ürün güncelleme, silme ve zimmet onay hareketleri gibi kritik işlemler zaman damgalı, kaynak ve seviye belirtilerek log tablolarında depolanmaktadır[cite: 1].

## 🛠️ Mimari ve Teknolojiler
* **Yazılım Mimarisi:** Veri akışının ve sistem katmanlarının kesin çizgilerle birbirinden ayrıldığı Çok Katmanlı Mimari (Multi-Layered Architecture) prensibine uygun olarak tasarlanmıştır[cite: 1].
* **Tasarım Deseni:** Sistem süreçleri MVC (Model-View-Controller) deseni üzerine kurulmuştur[cite: 1].
* **Backend:** Spring Boot, Spring Security, Spring Data JPA[cite: 1].
* **Veri Transfer Güvenliği:** Veritabanı modellerinin (Entity) doğrudan arayüze açılmasını engelleyen kurumsal DTO (Data Transfer Object) yapıları kullanılmıştır[cite: 1].
* **Frontend:** Sunucu tarafında dinamik sayfalar oluşturan Thymeleaf şablon motoru ve HTML tercih edilmiştir[cite: 1].
* **Veritabanı:** Normalize (3NF) edilmiş tablolarla MySQL 8.0 kullanılmıştır[cite: 1].
* **Test Süreçleri:** Sistemin kararlılığı ve iş kuralları JUnit 5 ve Mockito kütüphaneleri tabanlı birim testleri (Unit Tests) ile doğrulanmıştır[cite: 1].
