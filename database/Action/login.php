<?php

$returnArray=array();

if (empty($_REQUEST["kullaniciadi"]) || empty($_REQUEST["sifre"])) {
    $returnArray["status"] = "203";
    $returnArray["mesaj"] = "Gerekli bilgi veya bilgiler eksik.";
    echo json_encode($returnArray);
    return;
}


$kullaniciadi = htmlentities($_REQUEST["kullaniciadi"]);
$sifre = htmlentities($_REQUEST["sifre"]);


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

$user=$access->selectUser($kullaniciadi);

if (empty($user))
{
    $returnArray["status"] = "400";
    $returnArray["mesaj"] = "Kullanici bulunamadı.";
    echo json_encode($returnArray);
    return;

}

$guvenli_sifre=$user["sifre"];

//simdilik mail onayı olmadıgından emailonay"]==1 kontrolu 0 olucak
if ($user["emailonay"]==1 && $guvenli_sifre==$sifre){
    // kullanıcı bilgilerini dizide depolayıp daha sonra json olarak döndüreceğiz.
    $returnArray["status"] = "200";
    $returnArray["mesaj"] = "Kullanici girisi basarili bir sekilde yapildi.";
    $returnArray["id"] = $user["id"];
    $returnArray["kullaniciadi"] = $user["kullaniciadi"];
    $returnArray["mail"] = $user["mail"];
    $returnArray["adsoyad"] = $user["adsoyad"];
    $returnArray["avatar"] = $user["avatar"];
    
    echo json_encode($returnArray);
    
    return;

}
else if ($guvenli_sifre!=$sifre){
    $returnArray["status"] = "403";
    $returnArray["mesaj"] = "Sifre yanlıs.";
    echo json_encode($returnArray);
    return;
}

$access->disconnect();

echo json_encode($returnArray);



?>