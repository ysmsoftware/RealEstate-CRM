package com.ysminfosolution.realestate.service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.ysminfosolution.realestate.dto.ProjectDetailPdfView;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class HtmlProjectPdfRenderer {

    private final TemplateEngine templateEngine;
    private final S3StorageService s3StorageService;

    public byte[] renderPdf(ProjectDetailPdfView view) {
        String logoDataUri = resolveLogoDataUri(view.organizationLogoS3Key());

        Context context = new Context(Locale.ENGLISH);
        context.setVariable("view", view);
        context.setVariable("logoDataUri", logoDataUri);

        String html = templateEngine.process("project-detail", context);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(outputStream);
            builder.run();
            return outputStream.toByteArray();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to generate project detail PDF from HTML", ex);
        }
    }

    private String resolveLogoDataUri(String logoS3Key) {
        if (logoS3Key == null || logoS3Key.isBlank()) {
            return null;
        }

        try {
            S3StorageService.S3ObjectData logoObject = s3StorageService.downloadFile(logoS3Key);
            String contentType = normalizeContentType(logoObject.contentType(), logoS3Key);
            String base64 = Base64.getEncoder().encodeToString(logoObject.bytes());
            return "data:" + contentType + ";base64," + base64;
        } catch (Exception ex) {
            log.warn("Could not load organization logo from key {}: {}", logoS3Key, ex.getMessage());
            return null;
        }
    }

    private String normalizeContentType(String contentType, String key) {
        if (contentType != null && !contentType.isBlank()) {
            return contentType;
        }

        String normalizedKey = key == null ? "" : key.toLowerCase(Locale.ROOT);
        if (normalizedKey.endsWith(".png")) {
            return "image/png";
        }
        if (normalizedKey.endsWith(".jpg") || normalizedKey.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (normalizedKey.endsWith(".webp")) {
            return "image/webp";
        }
        return "image/png";
    }
}
