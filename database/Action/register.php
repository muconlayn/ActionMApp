<?php

$returnArray=array();

if (empty($_REQUEST["kullaniciadi"]) || empty($_REQUEST["sifre"]) || empty($_REQUEST["mail"]) || empty($_REQUEST["adsoyad"])) {
    $returnArray["status"] = "203";
    $returnArray["mesaj"] = "Gerekli bilgi veya bilgiler eksik.";
    echo json_encode($returnArray);
    return;
}


$kullaniciadi = htmlentities($_REQUEST["kullaniciadi"]);
$sifre = htmlentities($_REQUEST["sifre"]);
$mail = htmlentities($_REQUEST["mail"]);
$adsoyad = htmlentities($_REQUEST["adsoyad"]);

$salt = openssl_random_pseudo_bytes(20);
$guvenli_sifre =$sifre;


//veritabanı bağlantısı
require ("access.php");
$access = new access("localhost", "root", "", "action");
$baglanti=$access->connect();

if (!$baglanti)
{
    $returnArray["status"] = "404";
    $returnArray["mesaj"] = "Bağlantı başarısız.";
    echo json_encode($returnArray);
    return;
}

// veritabanına kullanıcı kayıt ediliyor
$result = $access->registerUser($kullaniciadi, $guvenli_sifre, $salt, $mail, $adsoyad);


if ($result){
//başarılı

    // kulanıcı adına göre sorgula ve kişi bilgilerini getir
    $user = $access->selectUser($kullaniciadi);

    // kullanıcı bilgilerini dizide depolayıp daha sonra json olarak döndüreceğiz.
    $returnArray["status"] = "200";
    $returnArray["mesaj"] = "Kullanici kaydi basarili bir sekilde yapildi.";
    $returnArray["id"] = $user["id"];
    $returnArray["kullaniciadi"] = $user["kullaniciadi"];
    $returnArray["mail"] = $user["mail"];
    $returnArray["adsoyad"] = $user["adsoyad"];
    $returnArray["avatar"] = $user["avatar"];
  

}else {

    $returnArray["status"] = "400";
    $returnArray["mesaj"] = "Verilen bilgilerle kayit yapilamadi. Kullanici adi veya mail adresi daha önce kullanilmis.";

}

// bağlantıyı kapatıyoruz
$access->disconnect();


// json olarak veriyi döndürüyoruz
echo json_encode($returnArray);




?>