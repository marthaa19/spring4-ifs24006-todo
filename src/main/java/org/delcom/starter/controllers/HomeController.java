package org.delcom.starter.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.Base64;

@RestController
public class HomeController {

    // Endpoint default
    @GetMapping("/")
    public String hello() {
        return "Hay Abdullah, selamat datang di pengembangan aplikasi dengan Spring Boot!";
    }

    // Endpoint untuk menyapa nama (dengan kondisi dinamis)
    @GetMapping("/hello/{name}")
    public String sayHello(@PathVariable String name) {
        // Branch dinamis: dua kemungkinan hasil tergantung input
        String hasil = name.isEmpty()
                ? "Nama tidak boleh kosong!"
                : "Hello, " + name + "!";
        return hasil;
    }

    // 1. Informasi NIM
    @GetMapping("/informasiNim/{nim}")
    public String informasiNim(@PathVariable String nim) {
        // Branch dinamis berdasarkan panjang nim
        String hasil = nim.length() < 5
                ? "NIM terlalu pendek!"
                : "Informasi NIM Anda adalah: " + nim;
        return hasil;
    }

    // 2. Perolehan Nilai dari Base64
    @GetMapping("/perolehanNilai/{dataBase64}")
    public String perolehanNilai(@PathVariable String dataBase64) {
        byte[] decoded = Base64.getDecoder().decode(dataBase64);
        String nilaiAsli = new String(decoded);

        // Branch: cek apakah nilainya numerik
        boolean isAngka = nilaiAsli.matches("\\d+");
        return isAngka
                ? "Nilai hasil decode dari Base64: " + nilaiAsli
                : "Data bukan angka valid: " + nilaiAsli;
    }

    // 3. Perbedaan L dan Kebalikannya
    @GetMapping("/perbedaanL/{strBase64}")
    public String perbedaanL(@PathVariable String strBase64) {
        byte[] decodedBytes = Base64.getDecoder().decode(strBase64);
        String teks = new String(decodedBytes);
        String reversed = new StringBuilder(teks).reverse().toString();

        // Branch: cek apakah palindrome
        boolean isPalindrome = teks.equalsIgnoreCase(reversed);
        return isPalindrome
                ? "Teks palindrome: " + teks
                : "Teks asli: " + teks + "<br>Teks kebalikannya: " + reversed;
    }

    // 4. Paling Ter (ubah ke huruf besar)
    @GetMapping("/palingTer/{dataBase64}")
    public String palingTer(@PathVariable String dataBase64) {
        byte[] decoded = Base64.getDecoder().decode(dataBase64);
        String teks = new String(decoded);
        String hasil = teks.toUpperCase();

        // Branch: cek apakah hasil sama dengan input (semua huruf besar)
        return teks.equals(hasil)
                ? "Teks sudah huruf besar: " + hasil
                : "Data paling terdepan (decode dari Base64): " + hasil;
    }
}
