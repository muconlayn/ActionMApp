-- phpMyAdmin SQL Dump
-- version 5.0.4
-- https://www.phpmyadmin.net/
--
-- Anamakine: 127.0.0.1
-- Üretim Zamanı: 16 Şub 2021, 21:07:34
-- Sunucu sürümü: 10.4.17-MariaDB
-- PHP Sürümü: 7.3.26

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Veritabanı: `action`
--

-- --------------------------------------------------------

--
-- Tablo için tablo yapısı `kisiler`
--

CREATE TABLE `kisiler` (
  `id` int(11) NOT NULL,
  `kullaniciadi` varchar(55) CHARACTER SET utf8 COLLATE utf8_turkish_ci NOT NULL,
  `sifre` varchar(25) COLLATE utf8_unicode_ci NOT NULL,
  `salt` binary(20) NOT NULL,
  `mail` varchar(30) COLLATE utf8_unicode_ci NOT NULL,
  `adsoyad` varchar(50) CHARACTER SET utf8 COLLATE utf8_turkish_ci NOT NULL,
  `avatar` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `emailonay` int(11) NOT NULL DEFAULT 1,
  `date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Tablo döküm verisi `kisiler`
--

INSERT INTO `kisiler` (`id`, `kullaniciadi`, `sifre`, `salt`, `mail`, `adsoyad`, `avatar`, `emailonay`, `date`) VALUES
(32, 'admin', 'admin', 0xe13c3dd05417929ec89d38e29c3f4241d391200d, 'admin@', 'admin', '', 1, '2021-02-16 19:56:14');

-- --------------------------------------------------------

--
-- Tablo için tablo yapısı `tweetler`
--

CREATE TABLE `tweetler` (
  `id` int(11) NOT NULL,
  `uuid` varchar(100) CHARACTER SET utf8 COLLATE utf8_turkish_ci NOT NULL,
  `text` varchar(500) CHARACTER SET utf8 COLLATE utf8_turkish_ci NOT NULL,
  `path` varchar(500) CHARACTER SET utf8 COLLATE utf8_turkish_ci NOT NULL,
  `date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dökümü yapılmış tablolar için indeksler
--

--
-- Tablo için indeksler `kisiler`
--
ALTER TABLE `kisiler`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `id` (`id`),
  ADD UNIQUE KEY `kullaniciadi` (`kullaniciadi`),
  ADD UNIQUE KEY `mail` (`mail`);

--
-- Tablo için indeksler `tweetler`
--
ALTER TABLE `tweetler`
  ADD UNIQUE KEY `uuid` (`uuid`);

--
-- Dökümü yapılmış tablolar için AUTO_INCREMENT değeri
--

--
-- Tablo için AUTO_INCREMENT değeri `kisiler`
--
ALTER TABLE `kisiler`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=35;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
