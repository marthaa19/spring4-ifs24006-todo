package org.delcom.starter.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class HomeControllerTest {

    private HomeController controller;

    @BeforeEach
    void setUp() {
        controller = new HomeController();
    }

    private String encodeBase64(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    // --- Hello Tests ---
    @Test
    @DisplayName("Hello endpoint returns correct message")
    void testHello() {
        assertEquals(
                "Hay Abdullah, selamat datang di pengembangan aplikasi dengan Spring Boot!",
                controller.hello()
        );
    }

    @Test
    @DisplayName("Hello with name returns personalized greeting")
    void testHelloWithName() {
        assertEquals("Hello, Abdullah!", controller.sayHello("Abdullah"));
    }

    // --- Informasi NIM Tests ---
    @Test
    @DisplayName("Informasi NIM valid")
    void testInformasiNimValid() {
        ResponseEntity<String> response = controller.informasiNim("11S24001");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Sarjana Informatika"));
    }

    @Test
    @DisplayName("Informasi NIM invalid length")
    void testInformasiNimInvalidLength() {
        ResponseEntity<String> response = controller.informasiNim("11S24");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Format NIM tidak valid. Harap masukkan 8 digit."));
    }

    @Test
    @DisplayName("Informasi NIM unknown prefix")
    void testInformasiNimUnknownPrefix() {
        ResponseEntity<String> response = controller.informasiNim("99924001");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Prefix NIM '999' tidak ditemukan."));
    }

    // --- Perolehan Nilai Tests (Menutup semua Grade dan Exception) ---

    // Grade A: Score >= 79.5
    @Test
    @DisplayName("Perolehan nilai menghasilkan Grade A")
    void testPerolehanNilaiA() {
        String input = String.join("\n", "10 15 10 15 20 30", "PA|100|80", "T|100|80", "K|100|80", "P|100|80", "UTS|100|80", "UAS|100|80", "---");
        ResponseEntity<String> response = controller.perolehanNilai(encodeBase64(input));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Grade: A"));
    }

    // Grade AB: Score >= 72.0
    @Test
    @DisplayName("Perolehan nilai menghasilkan Grade AB")
    void testPerolehanNilaiAB() {
        String input = String.join("\n", "10 15 10 15 20 30", "PA|100|72", "T|100|72", "K|100|72", "P|100|72", "UTS|100|72", "UAS|100|72", "---");
        ResponseEntity<String> response = controller.perolehanNilai(encodeBase64(input));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Grade: AB"));
    }

    // Grade B: Score >= 64.5
    @Test
    @DisplayName("Perolehan nilai menghasilkan Grade B")
    void testPerolehanNilaiB() {
        String input = String.join("\n", "10 15 10 15 20 30", "PA|100|65", "T|100|65", "K|100|65", "P|100|65", "UTS|100|65", "UAS|100|65", "---");
        ResponseEntity<String> response = controller.perolehanNilai(encodeBase64(input));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Grade: B"));
    }
    
    // Grade BC: Score >= 57.0
    @Test
    @DisplayName("Perolehan nilai menghasilkan Grade BC")
    void testPerolehanNilaiBC() {
        String input = String.join("\n", "10 15 10 15 20 30", "PA|100|57", "T|100|57", "K|100|57", "P|100|57", "UTS|100|57", "UAS|100|57", "---");
        ResponseEntity<String> response = controller.perolehanNilai(encodeBase64(input));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Grade: BC"));
    }

    // Grade C: Score >= 49.5
    @Test
    @DisplayName("Perolehan nilai menghasilkan Grade C")
    void testPerolehanNilaiC() {
        String input = String.join("\n", "10 15 10 15 20 30", "PA|100|50", "T|100|50", "K|100|50", "P|100|50", "UTS|100|50", "UAS|100|50", "---");
        ResponseEntity<String> response = controller.perolehanNilai(encodeBase64(input));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Grade: C"));
    }

    // Grade D: Score >= 34.0
    @Test
    @DisplayName("Perolehan nilai menghasilkan Grade D")
    void testPerolehanNilaiD() {
        String input = String.join("\n", "10 15 10 15 20 30", "PA|100|35", "T|100|35", "K|100|35", "P|100|35", "UTS|100|35", "UAS|100|35", "---");
        ResponseEntity<String> response = controller.perolehanNilai(encodeBase64(input));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Grade: D"));
    }

    // Grade E: Score < 34.0
    @Test
    @DisplayName("Perolehan nilai menghasilkan Grade E")
    void testPerolehanNilaiE() {
        String input = String.join("\n", "10 15 10 15 20 30", "PA|100|10", "T|100|10", "K|100|10", "P|100|10", "UTS|100|10", "UAS|100|10", "---");
        ResponseEntity<String> response = controller.perolehanNilai(encodeBase64(input));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Grade: E"));
    }
    
    // Menutup branch max > 0 ? ... : 0 untuk semua komponen
    @Test
    @DisplayName("Perolehan nilai dengan pembagi (max) nol untuk semua komponen")
    void testPerolehanNilaiAllMaxZero() {
        String input = String.join("\n", "10 15 10 15 20 30", "PA|0|0", "T|0|0", "K|0|0", "P|0|0", "UTS|0|0", "UAS|0|0", "---");
        ResponseEntity<String> response = controller.perolehanNilai(encodeBase64(input));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains(">> Nilai Akhir: 0.00"));
        assertTrue(response.getBody().contains("Grade: E"));
    }

    // Menutup NoSuchElementException (missing "---")
    @Test
    @DisplayName("Perolehan nilai input tidak lengkap/salah format (missing '---')")
    void testPerolehanNilaiInvalidInputFormat() {
        String input = "10 15 10 15 20 30"; 
        ResponseEntity<String> response = controller.perolehanNilai(encodeBase64(input));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Format data input tidak valid atau tidak lengkap."));
    }

    // Menutup ArrayIndexOutOfBoundsException (parts.length < 3)
    @Test
    @DisplayName("Perolehan nilai satu baris input tidak lengkap (memicu ArrayIndexOutOfBoundsException)")
    void testPerolehanNilaiSingleLineInvalidFormat() {
        String input = String.join("\n", "10 15 10 15 20 30", "PA|100", "---");
        ResponseEntity<String> response = controller.perolehanNilai(encodeBase64(input));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Format data input tidak valid atau tidak lengkap."));
    }
    
    // Menutup NumberFormatException (Integer.parseInt)
    @Test
    @DisplayName("Perolehan nilai input non-numerik (memicu NumberFormatException)")
    void testPerolehanNilaiNonNumericInput() {
        String input = String.join("\n", "10 15 10 15 20 30", "PA|A|80", "---");
        ResponseEntity<String> response = controller.perolehanNilai(encodeBase64(input));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Format data input tidak valid atau tidak lengkap."));
    }

    @Test
    @DisplayName("Perolehan nilai input Base64 tidak valid")
    void testPerolehanNilaiInvalidBase64() {
        ResponseEntity<String> response = controller.perolehanNilai("invalid-base64-string$$$");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Input Base64 tidak valid."));
    }
    
    // BARU: Menutup branch simbol nilai yang tidak valid (case yang tidak cocok di switch)
    @Test
    @DisplayName("Perolehan nilai dengan simbol yang tidak dikenal (memicu missing branch)")
    void testPerolehanNilaiUnknownSymbol() {
        // Simbol "X" tidak cocok dengan case manapun (PA, T, K, P, UTS, UAS)
        String input = String.join("\n", "10 15 10 15 20 30", "X|10|10", "PA|100|100", "---");
        ResponseEntity<String> response = controller.perolehanNilai(encodeBase64(input));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Nilai akhir hanya dari PA: (100/100) * 10 = 10.00. X diabaikan. Total weight = 100.
        // PA=10% -> 10.00, T=0, K=0, P=0, UTS=0, UAS=0. Final Score = 10.00
        assertTrue(response.getBody().contains(">> Nilai Akhir: 10.00"));
    }


    // --- Perbedaan L (Matrix) Tests (Menutup semua branch N=1, N=2, N=3) ---
    // Menutup N=3 (Ganjil, loop j berjalan, diff=0/dom=center)
    @Test
    @DisplayName("Matrix N=3 (Ganjil) dengan diff=0")
    void testPerbedaanLValidN3DiffZero() {
        String input = String.join("\n", "3", "1 2 3", "4 5 6", "7 8 9"); 
        ResponseEntity<String> response = controller.perbedaanL(encodeBase64(input));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Dominan: 5")); 
    }

    // Menutup N=2 (Genap, loop j diskip, diff>0, dom=max)
    @Test
    @DisplayName("Matrix N=2 (Genap) dengan diff>0")
    void testPerbedaanLValidN2Genap() {
        String input = String.join("\n", "2", "1 2", "3 4"); 
        ResponseEntity<String> response = controller.perbedaanL(encodeBase64(input));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Nilai Tengah: 0")); 
        assertTrue(response.getBody().contains("Dominan: 6")); 
    }
    
    // Menutup N=1 (Loop j diskip, diff=0, dom=center)
    @Test
    @DisplayName("Matrix N=1 (Loop J diskip)")
    void testPerbedaanLValidN1() {
        String input = String.join("\n", "1", "1"); 
        ResponseEntity<String> response = controller.perbedaanL(encodeBase64(input));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Nilai L: 1"));
        assertTrue(response.getBody().contains("Nilai Kebalikan L: 1"));
        assertTrue(response.getBody().contains("Nilai Tengah: 1")); 
        assertTrue(response.getBody().contains("Dominan: 1")); 
    }

    // Menutup NoSuchElementException (input tidak lengkap)
    @Test
    @DisplayName("Matrix perbedaanL input tidak lengkap (memicu NoSuchElementException)")
    void testPerbedaanLInvalidInputFormat() {
        String input = String.join("\n", "3", "1 2 3", "4 5 6", "7 8"); // Kurang 1 angka
        ResponseEntity<String> response = controller.perbedaanL(encodeBase64(input));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Format data matriks tidak valid atau tidak lengkap."));
    }

    @Test
    @DisplayName("Matrix perbedaanL input Base64 tidak valid")
    void testPerbedaanLInvalidBase64() {
        ResponseEntity<String> response = controller.perbedaanL("invalid-base64-matrix$$$");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Input Base64 tidak valid."));
    }

    // --- Paling Ter Tests ---
    @Test
    @DisplayName("PalingTer valid")
    void testPalingTerValid() {
        String input = "1 2 3 3 4 1"; 
        ResponseEntity<String> response = controller.palingTer(encodeBase64(input));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Tertinggi: 4"));
        assertTrue(response.getBody().contains("Terendah: 1"));
        assertTrue(response.getBody().contains("Terbanyak: 1 (2x)")); 
        assertTrue(response.getBody().contains("Tersedikit: 2 (1x)")); 
    }

    @Test
    @DisplayName("PalingTer input kosong (menutup list.isEmpty())")
    void testPalingTerEmptyInput() {
        String input = ""; 
        ResponseEntity<String> response = controller.palingTer(encodeBase64(input));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Tidak ada input", response.getBody());
    }

    @Test
    @DisplayName("PalingTer input Base64 tidak valid")
    void testPalingTerInvalidBase64() {
        ResponseEntity<String> response = controller.palingTer("invalid-base64-palingter$$$");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Input Base64 tidak valid."));
    }
}