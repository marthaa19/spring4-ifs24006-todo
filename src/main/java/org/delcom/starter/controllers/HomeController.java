package org.delcom.starter.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Base64;
import java.util.NoSuchElementException;

@RestController
public class HomeController {

    // Threshold Grades (Sudah teruji penuh dengan test case baru)
    private static final double GRADE_A_THRESHOLD = 79.5;
    private static final double GRADE_AB_THRESHOLD = 72.0;
    private static final double GRADE_B_THRESHOLD = 64.5;
    private static final double GRADE_BC_THRESHOLD = 57.0;
    private static final double GRADE_C_THRESHOLD = 49.5;
    private static final double GRADE_D_THRESHOLD = 34.0;

    private static final Map<String, String> PROGRAM_STUDI_MAP = Map.ofEntries(
            Map.entry("11S", "Sarjana Informatika"),
            Map.entry("12S", "Sarjana Sistem Informasi"),
            Map.entry("14S", "Sarjana Teknik Elektro"),
            Map.entry("21S", "Sarjana Manajemen Rekayasa"),
            Map.entry("22S", "Sarjana Teknik Metalurgi"),
            Map.entry("31S", "Sarjana Teknik Bioproses"),
            Map.entry("114", "Diploma 4 Teknologi Rekasaya Perangkat Lunak"),
            Map.entry("113", "Diploma 3 Teknologi Informasi"),
            Map.entry("133", "Diploma 3 Teknologi Komputer")
    );

    @GetMapping("/")
    public String hello() {
        return "Hay Abdullah, selamat datang di pengembangan aplikasi dengan Spring Boot!";
    }

    @GetMapping("/hello/{name}")
    public String sayHello(@PathVariable String name) {
        return "Hello, " + name + "!";
    }

    @GetMapping("/informasiNim/{nim}")
    public ResponseEntity<String> informasiNim(@PathVariable String nim) {
        try {
            String result = processNimInfo(nim);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/perolehanNilai/{strBase64}")
    public ResponseEntity<String> perolehanNilai(@PathVariable String strBase64) {
        try {
            String decodedInput = decodeBase64(strBase64);
            String result = processNilai(decodedInput);
            return ResponseEntity.ok(result);
        } catch (NoSuchElementException | ArrayIndexOutOfBoundsException | NumberFormatException e) {
            // Menangkap semua error format input nilai yang tidak valid
            return new ResponseEntity<>(
                    "Format data input tidak valid atau tidak lengkap. Pastikan angka dan format sudah benar.",
                    HttpStatus.BAD_REQUEST
            );
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Input Base64 tidak valid.", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/perbedaanL/{strBase64}")
    public ResponseEntity<String> perbedaanL(@PathVariable String strBase64) {
        try {
            String decodedInput = decodeBase64(strBase64);
            String result = processMatrix(decodedInput);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Input Base64 tidak valid.", HttpStatus.BAD_REQUEST);
        } catch (NoSuchElementException e) {
            // Menangkap error jika input matriks tidak lengkap (termasuk n, tapi kurang angka)
            return new ResponseEntity<>("Format data matriks tidak valid atau tidak lengkap.", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/palingTer/{strBase64}")
    public ResponseEntity<String> palingTer(@PathVariable String strBase64) {
        try {
            String decodedInput = decodeBase64(strBase64);
            String result = processPalingTer(decodedInput);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Input Base64 tidak valid.", HttpStatus.BAD_REQUEST);
        }
    }

    // ---------- Helper ----------
    private String decodeBase64(String strBase64) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(strBase64);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalArgumentException("Input Base64 tidak valid: " + e.getMessage());
        }
    }

    private String getGrade(double score) {
        if (score >= GRADE_A_THRESHOLD) return "A";
        else if (score >= GRADE_AB_THRESHOLD) return "AB";
        else if (score >= GRADE_B_THRESHOLD) return "B";
        else if (score >= GRADE_BC_THRESHOLD) return "BC";
        else if (score >= GRADE_C_THRESHOLD) return "C";
        else if (score >= GRADE_D_THRESHOLD) return "D";
        else return "E";
    }

    // ---------- Logic ----------
    private String processNimInfo(String nim) {
        if (nim.length() != 8)
            throw new IllegalArgumentException("Format NIM tidak valid. Harap masukkan 8 digit.");

        String prefix = nim.substring(0, 3);
        String angkatanStr = nim.substring(3, 5);
        String nomorUrut = nim.substring(5);
        String namaProgramStudi = PROGRAM_STUDI_MAP.get(prefix);

        if (namaProgramStudi == null)
            throw new IllegalArgumentException("Prefix NIM '" + prefix + "' tidak ditemukan.");

        int tahunAngkatan = 2000 + Integer.parseInt(angkatanStr);

        return "Informasi NIM " + nim + ":\n" +
                ">> Program Studi: " + namaProgramStudi + "\n" +
                ">> Angkatan: " + tahunAngkatan + "\n" +
                ">> Urutan: " + Integer.parseInt(nomorUrut);
    }

    private String processNilai(String input) {
        StringBuilder sb = new StringBuilder();

        try (Scanner scanner = new Scanner(input)) {
            scanner.useLocale(Locale.US);

            // Membaca 6 bobot. Jika input terpotong di sini, NoSuchElementException terlempar.
            int paWeight = scanner.nextInt();
            int assignmentWeight = scanner.nextInt();
            int quizWeight = scanner.nextInt();
            int projectWeight = scanner.nextInt();
            int midExamWeight = scanner.nextInt();
            int finalExamWeight = scanner.nextInt();
            
            // Mengonsumsi sisa baris bobot (jika ada)
            if (scanner.hasNextLine()) {
                 scanner.nextLine();
            }

            int totalPA = 0, maxPA = 0;
            int totalT = 0, maxT = 0;
            int totalK = 0, maxK = 0;
            int totalP = 0, maxP = 0;
            int totalUTS = 0, maxUTS = 0;
            int totalUAS = 0, maxUAS = 0;

            boolean foundSeparator = false;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                
                if (line.equals("---")) {
                    foundSeparator = true;
                    break;
                }
                
                String[] parts = line.split("\\|"); 
                
                // Menutup branch ArrayIndexOutOfBoundsException (parts.length < 3)
                if (parts.length < 3) {
                     throw new ArrayIndexOutOfBoundsException("Baris nilai tidak memiliki 3 bagian: " + line);
                }
                
                String symbol = parts[0].trim(); // Trim symbol untuk handle input " X |10|10"
                // Menutup branch NumberFormatException (Integer.parseInt)
                int max = Integer.parseInt(parts[1].trim()); 
                int val = Integer.parseInt(parts[2].trim());

                switch (symbol) {
                    case "PA" -> {
                        totalPA += val;
                        maxPA += max;
                    }
                    case "T" -> {
                        totalT += val;
                        maxT += max;
                    }
                    case "K" -> {
                        totalK += val;
                        maxK += max;
                    }
                    case "P" -> {
                        totalP += val;
                        maxP += max;
                    }
                    case "UTS" -> {
                        totalUTS += val;
                        maxUTS += max;
                    }
                    case "UAS" -> {
                        totalUAS += val;
                        maxUAS += max;
                    }
                }
            }
            
            // Menutup branch NoSuchElementException (missing "---")
            if (!foundSeparator) {
                throw new NoSuchElementException("Separator '---' tidak ditemukan atau input tidak lengkap.");
            }

            // Menutup branch Division by Zero (max > 0 ? ... : 0)
            double scorePA = maxPA > 0 ? (totalPA * 100.0 / maxPA) : 0;
            double scoreT = maxT > 0 ? (totalT * 100.0 / maxT) : 0;
            double scoreK = maxK > 0 ? (totalK * 100.0 / maxK) : 0;
            double scoreP = maxP > 0 ? (totalP * 100.0 / maxP) : 0;
            double scoreUTS = maxUTS > 0 ? (totalUTS * 100.0 / maxUTS) : 0;
            double scoreUAS = maxUAS > 0 ? (totalUAS * 100.0 / maxUAS) : 0;
            

            double finalScore =
                    (scorePA) * paWeight / 100 +
                    (scoreT) * assignmentWeight / 100 +
                    (scoreK) * quizWeight / 100 +
                    (scoreP) * projectWeight / 100 +
                    (scoreUTS) * midExamWeight / 100 +
                    (scoreUAS) * finalExamWeight / 100;

            sb.append("Perolehan Nilai:\n");
            sb.append(String.format(Locale.US, ">> Partisipatif: %.0f/100 (%.2f/%d)\n",
                    scorePA, scorePA * paWeight / 100, paWeight));
            sb.append(String.format(Locale.US, ">> Tugas: %.0f/100 (%.2f/%d)\n",
                    scoreT, scoreT * assignmentWeight / 100, assignmentWeight));
            sb.append(String.format(Locale.US, ">> Kuis: %.0f/100 (%.2f/%d)\n",
                    scoreK, scoreK * quizWeight / 100, quizWeight));
            sb.append(String.format(Locale.US, ">> Proyek: %.0f/100 (%.2f/%d)\n",
                    scoreP, scoreP * projectWeight / 100, projectWeight));
            sb.append(String.format(Locale.US, ">> UTS: %.0f/100 (%.2f/%d)\n",
                    scoreUTS, scoreUTS * midExamWeight / 100, midExamWeight));
            sb.append(String.format(Locale.US, ">> UAS: %.0f/100 (%.2f/%d)\n",
                    scoreUAS, scoreUAS * finalExamWeight / 100, finalExamWeight));
            sb.append("\n");
            sb.append(String.format(Locale.US, ">> Nilai Akhir: %.2f\n", finalScore));
            sb.append(String.format(Locale.US, ">> Grade: %s", getGrade(finalScore)));
        }

        return sb.toString().trim();
    }

    private String processMatrix(String input) {
        Scanner sc = new Scanner(input);
        int n = sc.nextInt();
        int[][] m = new int[n][n];

        // Menutup loop pembacaan matriks
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                m[i][j] = sc.nextInt();

        sc.close(); 

        int sumL = 0, sumRevL = 0;
        // Sisi kiri (i=0..n-1, j=0)
        for (int i = 0; i < n; i++) sumL += m[i][0];
        // Bawah (i=n-1, j=1..n-2) - Tertutup oleh N=3 dan N=1
        for (int j = 1; j < n - 1; j++) sumL += m[n - 1][j];

        // Sisi Kanan (i=0..n-1, j=n-1)
        for (int i = 0; i < n; i++) sumRevL += m[i][n - 1];
        // Atas (i=0, j=1..n-2) - Tertutup oleh N=3 dan N=1
        for (int j = 1; j < n - 1; j++) sumRevL += m[0][j];

        // Center (menutup branch ganjil/genap)
        int center = n % 2 != 0 ? m[n / 2][n / 2] : 0; 
        int diff = Math.abs(sumL - sumRevL);
        // Dominan (menutup branch diff=0 / diff>0)
        int dom = diff == 0 ? center : Math.max(sumL, sumRevL);

        return String.format(
                "Nilai L: %d\nNilai Kebalikan L: %d\nNilai Tengah: %d\nPerbedaan: %d\nDominan: %d",
                sumL, sumRevL, center, diff, dom
        );
    }

    private String processPalingTer(String input) {
        Scanner sc = new Scanner(input);
        List<Integer> list = new ArrayList<>();

        while (sc.hasNextInt())
            list.add(sc.nextInt());

        sc.close();

        // Menutup branch list.isEmpty()
        if (list.isEmpty())
            return "Tidak ada input";

        Map<Integer, Integer> freq = new LinkedHashMap<>();
        for (int x : list)
            freq.put(x, freq.getOrDefault(x, 0) + 1);

        int maxVal = Collections.max(list);
        int minVal = Collections.min(list);
        int mostVal = freq.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();
        
        int minFreq = Collections.min(freq.values());
        int tersedikit = freq.entrySet().stream()
                .filter(entry -> entry.getValue().equals(minFreq))
                .map(Map.Entry::getKey)
                .findFirst() 
                .orElse(0); 

        return String.format(
                "Tertinggi: %d\nTerendah: %d\nTerbanyak: %d (%dx)\nTersedikit: %d (%dx)",
                maxVal, minVal, mostVal, freq.get(mostVal), tersedikit, freq.get(tersedikit)
        );
    }
}