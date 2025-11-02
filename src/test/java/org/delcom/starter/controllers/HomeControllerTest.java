package org.delcom.starter.controllers;

import org.junit.jupiter.api.Test;
import java.util.Base64;
import static org.junit.jupiter.api.Assertions.*;

public class HomeControllerTest {

    HomeController controller = new HomeController();

    @Test
    void testHello() {
        assertTrue(controller.hello().contains("Spring Boot"));
    }

    // === Branch untuk sayHello ===
    @Test
    void testSayHelloNormal() {
        assertEquals("Hello, Martha!", controller.sayHello("Martha"));
    }

    @Test
    void testSayHelloEmpty() {
        assertTrue(controller.sayHello("").contains("tidak boleh kosong"));
    }

    // === Branch untuk informasiNim ===
    @Test
    void testInformasiNimValid() {
        assertTrue(controller.informasiNim("IF24006").contains("IF24006"));
    }

    @Test
    void testInformasiNimPendek() {
        assertTrue(controller.informasiNim("123").contains("pendek"));
    }

    // === Branch untuk perolehanNilai ===
    @Test
    void testPerolehanNilaiAngka() {
        String encoded = Base64.getEncoder().encodeToString("95".getBytes());
        assertTrue(controller.perolehanNilai(encoded).contains("95"));
    }

    @Test
    void testPerolehanNilaiNonAngka() {
        String encoded = Base64.getEncoder().encodeToString("A+".getBytes());
        assertTrue(controller.perolehanNilai(encoded).contains("bukan angka"));
    }

    // === Branch untuk perbedaanL ===
    @Test
    void testPerbedaanLNormal() {
        String encoded = Base64.getEncoder().encodeToString("Java".getBytes());
        assertTrue(controller.perbedaanL(encoded).contains("avaJ"));
    }

    @Test
    void testPerbedaanLPalindrome() {
        String encoded = Base64.getEncoder().encodeToString("aba".getBytes());
        assertTrue(controller.perbedaanL(encoded).contains("palindrome"));
    }

    // === Branch untuk palingTer ===
    @Test
    void testPalingTerNormal() {
        String encoded = Base64.getEncoder().encodeToString("hello".getBytes());
        assertTrue(controller.palingTer(encoded).contains("HELLO"));
    }

    @Test
    void testPalingTerSudahUpper() {
        String encoded = Base64.getEncoder().encodeToString("HELLO".getBytes());
        assertTrue(controller.palingTer(encoded).contains("sudah huruf besar"));
    }
}
