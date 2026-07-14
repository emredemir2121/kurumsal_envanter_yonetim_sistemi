# Kurumsal Envanter Yönetim Sistemi

**Geliştirici:** Cem Emre Demir
**Kurum:** Işık Üniversitesi

## 📌 Proje Özeti
Bu proje, bir kuruluşun teknolojik varlıklarını ve personel zimmet süreçlerini uçtan uca yönetmek amacıyla geliştirilmiş bir Kurumsal Envanter Yönetim Sistemidir. Sistem; manuel envanter takibinde oluşabilecek veri kayıplarını önlemeyi, stok yönetimini dijital ortama taşımayı ve idari birimler ile personel arasındaki zimmet süreçlerini güvenli bir web platformu üzerinden yönetmeyi hedefler.

## 🚀 Temel Özellikler
* **Güvenlik ve Rol Tabanlı Erişim (RBAC):** Sistem güvenliği Spring Security altyapısı kullanılarak sağlanmış olup, Yönetici (Admin) ve Personel (User) olmak üzere iki temel yetki seviyesinden oluşmaktadır.
* **Gelişmiş Yönetici (Admin) Paneli:** Zimmet taleplerinin onay/red işlemleri, personel bilgileri yönetimi, ürün girişleri, kritik stok takibi ve tedarikçi yönetimi bu panel üzerinden gerçekleştirilmektedir.
* **Kişiselleştirilmiş Personel Paneli:** Kullanıcıların kendilerine zimmetli cihazları görüntülemesine, yeni donanım zimmet talebi oluşturmasına ve iade süreçlerini dijital ortamda başlatmasına olanak sağlar.
* **Otomatik Stok Senkronizasyonu:** Bir zimmet talebi onaylandığında ürün stoğu otomatik olarak düşürülerek veritabanı seviyesinde veri tutarlılığı (Transactional) korunmaktadır.
* **Merkezi Günlükleme ve Denetim (Audit Logging):** Ürün güncelleme, silme ve zimmet onay hareketleri gibi kritik işlemler zaman damgalı, kaynak ve seviye belirtilerek log tablolarında depolanmaktadır.

## 🛠️ Mimari ve Teknolojiler
* **Yazılım Mimarisi:** Veri akışının ve sistem katmanlarının kesin çizgilerle birbirinden ayrıldığı Çok Katmanlı Mimari (Multi-Layered Architecture) prensibine uygun olarak tasarlanmıştır.
* **Tasarım Deseni:** Sistem süreçleri MVC (Model-View-Controller) deseni üzerine kurulmuştur.
* **Backend:** Spring Boot, Spring Security, Spring Data JPA.
* **Veri Transfer Güvenliği:** Veritabanı modellerinin (Entity) doğrudan arayüze açılmasını engelleyen kurumsal DTO (Data Transfer Object) yapıları kullanılmıştır.
* **Frontend:** Sunucu tarafında dinamik sayfalar oluşturan Thymeleaf şablon motoru ve HTML tercih edilmiştir.
* **Veritabanı:** Normalize (3NF) edilmiş tablolarla MySQL 8.0 kullanılmıştır[cite: 1].
* **Test Süreçleri:** Sistemin kararlılığı ve iş kuralları JUnit 5 ve Mockito kütüphaneleri tabanlı birim testleri (Unit Tests) ile doğrulanmıştır[cite: 1].
