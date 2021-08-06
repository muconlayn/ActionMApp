<?php

 require 'PHPMailer/class.phpmailer.php';
 require 'PHPMailer/PHPMailerAutoload.php';


class email{


    function tokenOlustur($uzunluk){

        $karakterler="qwertyuopasdfghjklzxcvbnmiQWERTYUIOPASDFGHJKLZXCVBNM1234567890";
        $karaktersayisi=strlen($karakterler);

        $token="";

        for ($i=0; $i<$uzunluk; $i++){
            $token.=$karakterler[rand(0,$karaktersayisi-1)];
        }

        return $token;
    }


    function emailOnaySablonu() {

        // dosyayı açıyoruz
        $file = fopen("sablonlar/emailOnayMailSablonu.html", "r") or die("Dosya açma işlemi başarısız.");

        // dosya içeriğini $sablon içerisine sakla
        $sablon = fread($file, filesize("sablonlar/emailOnayMailSablonu.html"));

        fclose($file);

        return $sablon;

    }

     function sifreSifirlamaMailSablonu() {

        // dosyayı açıyoruz
        $file = fopen("sablonlar/sifreSifirlamaMailSablonu.html", "r") or die("Dosya açma işlemi başarısız.");

        // dosya içeriğini $sablon içerisine sakla
        $sablon = fread($file, filesize("sablonlar/sifreSifirlamaMailSablonu.html"));

        fclose($file);

        return $sablon;

    }




    function sendEmail($detay) {

        $konu = $detay["konu"]; //subject
        $kime = $detay["kime"]; //to
        $fromName = $detay["fromName"]; //gönderici ismi
        $fromEmail = $detay["fromEmail"]; //gönderici mail adresi
        $body = $detay["body"]; //gövde

        //başlık veya üstbilgi
        $headers = "MIME-Version: 1.0" . "\r\n";
        $headers .= "Content-type:text/html;content=UTF-8" . "\r\n";
        $headers .= "From: " . $fromName . " <" . $fromEmail . ">" . "\r\n";

        //mail gönderiliyor
        /*if(mail($kime, $konu, $body, $headers)){
        	return "Mail gönderildi";
        }else{
        	return "Mail gonderilemedi";
        }*/



    }


    function sendEmailPhpMailer($detay){

        $konu = $detay["konu"]; //subject
        $kime = $detay["kime"]; //to
        $body = $detay["body"]; //gövde
        $fromName = $detay["fromName"]; //gönderici ismi
        
        
        $mail = new PHPMailer();

        $mail->IsSMTP();
        $mail->CharSet = 'UTF-8';
        $mail->Host = 'smtp.gmail.com’';
        $mail->SMTPDebug = 2;  // Hata ayıklama değişkeni: 1 = hata ve mesaj gösterir, 2 = sadece mesaj gösterir
        $mail->SMTPAuth = true;
        $mail->Port = 587;
        $mail->Username = 'actionuygulama@gmail.com';
        $mail->Password = 'actionuygulaması';
        $mail->SMTPSecure = 'tls';
        $mail->SetFrom($mail->Username , $fromName);
        $mail->isHTML(true);



        $mail->Subject = $konu;
        $mail->MsgHTML($body);
        $mail->AddAddress($kime);


        if (!$mail->Send()) {
            return "Mailer Hata: " . $mail->ErrorInfo;
        } else {
            return "Mail başarıyla gönderildi!";
        }

}

}
?>