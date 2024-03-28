package com.yogi.account.Account.controller;

import com.yogi.account.Account.service.AccountManagementService;
import com.yogi.account.Account.serviceImpl.SummaryPdf;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileOutputStream;
import java.io.IOException;


@AllArgsConstructor
@RestController
public class AccountController {

   private final SummaryPdf summaryPdf;
   private final AccountManagementService accountManagementService;

    @GetMapping("/uploadpdf/{filename}")
    public String uploadpdf(@PathVariable String filename, @RequestBody byte[] filecontent) {
        try (FileOutputStream fileOutputStream = new FileOutputStream("src/main/resources/UplodedFiles" + filename)) {
            fileOutputStream.write(filecontent);

            return "PDF file saved successfully.";
        } catch (Exception e) {
            return ("Error saving PDF file: " + e.getMessage());

        }
    }
        @GetMapping("/downloadpdf/{filename}")
        public ResponseEntity<byte[]> downloadPdf (@PathVariable String filename) throws IOException {
            byte[] bytes = summaryPdf.drawSummary();
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename +".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(bytes);
                    }

    @GetMapping("/elss")
    public String elss () throws Exception {
       return accountManagementService.addElssStatementData();
    }

    @GetMapping("/addDetails")
    public String addStatementDetails (@RequestParam String task) throws Exception {
        String responseSbiService = accountManagementService.fetchSbiStatementData(task);
        String responseUbiService = accountManagementService.fetchSbiStatementData(task);
        return responseSbiService + "\n " + responseUbiService;
    }






}
