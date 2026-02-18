package com.ysminfosolution.realestate.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProjectPdfRenderer {

    public record ExternalDocument(
            String documentLabel,
            String documentType,
            String key,
            String contentType,
            byte[] bytes) {
    }


    public record MergedPdfResult(byte[] pdfBytes, int mergedPdfCount, int mergedImageCount, int skippedCount) {
    }

    private static final PDRectangle PAGE_SIZE = PDRectangle.A4;
    private static final float MARGIN = 50f;

    public MergedPdfResult mergeWithExternalDocuments(byte[] mainPdfBytes, List<ExternalDocument> externalDocuments) {
        if (externalDocuments == null || externalDocuments.isEmpty()) {
            return new MergedPdfResult(mainPdfBytes, 0, 0, 0);
        }

        PDFMergerUtility mergerUtility = new PDFMergerUtility();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            mergerUtility.setDestinationStream(outputStream);
            mergerUtility.addSource(new ByteArrayInputStream(mainPdfBytes));

            int mergedPdfCount = 0;
            int mergedImageCount = 0;
            int skippedCount = 0;

            for (ExternalDocument externalDocument : externalDocuments) {
                if (externalDocument == null || externalDocument.bytes() == null || externalDocument.bytes().length == 0) {
                    skippedCount++;
                    continue;
                }

                try {
                    if (isPdf(externalDocument.contentType(), externalDocument.key())) {
                        byte[] annotatedPdfBytes = addHeaderToFirstPagePdf(
                                externalDocument.bytes(),
                                externalDocument.documentLabel(),
                                externalDocument.documentType());
                        mergerUtility.addSource(new ByteArrayInputStream(annotatedPdfBytes));
                        mergedPdfCount++;
                        continue;
                    }

                    if (isImage(externalDocument.contentType(), externalDocument.key())) {
                        byte[] imagePdf = imageToSinglePagePdfWithHeader(
                                externalDocument.bytes(),
                                externalDocument.documentLabel(),
                                externalDocument.documentType());
                        mergerUtility.addSource(new ByteArrayInputStream(imagePdf));
                        mergedImageCount++;
                        continue;
                    }

                    skippedCount++;
                    log.warn("Skipping unsupported content type for document {}", externalDocument.documentLabel());
                } catch (Exception ex) {
                    skippedCount++;
                    log.warn(
                            "Skipping external document {} due to rendering issue: {}",
                            externalDocument.documentLabel(),
                            ex.getMessage());
                }
            }

            mergerUtility.mergeDocuments(MemoryUsageSetting.setupMixed(20L * 1024L * 1024L));

            return new MergedPdfResult(outputStream.toByteArray(), mergedPdfCount, mergedImageCount, skippedCount);

        } catch (IOException ex) {
            throw new IllegalStateException("Failed to merge external documents into generated PDF", ex);
        }
    }

    private byte[] imageToSinglePagePdfWithHeader(byte[] imageBytes, String imageName, String imageType) throws IOException {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PAGE_SIZE);
            document.addPage(page);

            PDImageXObject image = PDImageXObject.createFromByteArray(document, imageBytes, defaultString(imageName));

            float topY = PAGE_SIZE.getHeight() - MARGIN;
            float headerBlockHeight = 44f;
            float availableTop = topY - headerBlockHeight;
            float maxWidth = PAGE_SIZE.getWidth() - (2 * MARGIN);
            float maxHeight = availableTop - MARGIN;

            float widthScale = maxWidth / image.getWidth();
            float heightScale = maxHeight / image.getHeight();
            float scale = Math.min(widthScale, heightScale);

            float drawWidth = image.getWidth() * scale;
            float drawHeight = image.getHeight() * scale;
            float x = (PAGE_SIZE.getWidth() - drawWidth) / 2f;
            float y = MARGIN + ((maxHeight - drawHeight) / 2f);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float textY = topY;

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.newLineAtOffset(MARGIN, textY);
                contentStream.showText("Document: " + defaultString(imageName));
                contentStream.endText();

                textY -= 18f;

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                contentStream.newLineAtOffset(MARGIN, textY);
                contentStream.showText("Type: " + defaultString(imageType));
                contentStream.endText();

                contentStream.drawImage(image, x, y, drawWidth, drawHeight);
            }

            document.save(outputStream);
            return outputStream.toByteArray();
        }
    }

    @SuppressWarnings("deprecation")
    private byte[] addHeaderToFirstPagePdf(byte[] pdfBytes, String documentLabel, String documentType) throws IOException {
        try (PDDocument document = PDDocument.load(pdfBytes); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            if (document.getNumberOfPages() == 0) {
                return pdfBytes;
            }

            PDPage firstPage = document.getPage(0);
            PDRectangle mediaBox = firstPage.getMediaBox();
            float pageHeight = mediaBox.getHeight();
            float pageWidth = mediaBox.getWidth();
            float stripHeight = 42f;
            float stripY = pageHeight - stripHeight;

            try (PDPageContentStream contentStream = new PDPageContentStream(
                    document,
                    firstPage,
                    PDPageContentStream.AppendMode.APPEND,
                    true,
                    true)) {

                contentStream.setNonStrokingColor(255, 255, 255);
                contentStream.addRect(0, stripY, pageWidth, stripHeight);
                contentStream.fill();

                contentStream.setStrokingColor(220, 220, 220);
                contentStream.moveTo(0, stripY);
                contentStream.lineTo(pageWidth, stripY);
                contentStream.stroke();

                float textY = pageHeight - 16f;

                contentStream.beginText();
                contentStream.setNonStrokingColor(15, 23, 42);
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
                contentStream.newLineAtOffset(MARGIN, textY);
                contentStream.showText("Document: " + defaultString(documentLabel));
                contentStream.endText();

                contentStream.beginText();
                contentStream.setNonStrokingColor(71, 85, 105);
                contentStream.setFont(PDType1Font.HELVETICA, 8);
                contentStream.newLineAtOffset(MARGIN, textY - 12f);
                contentStream.showText("Type: " + defaultString(documentType));
                contentStream.endText();
            }

            document.save(outputStream);
            return outputStream.toByteArray();
        }
    }

    private boolean isPdf(String contentType, String key) {
        String normalizedContentType = contentType == null ? "" : contentType.toLowerCase(Locale.ROOT);
        String normalizedKey = key == null ? "" : key.toLowerCase(Locale.ROOT);

        return normalizedContentType.startsWith("application/pdf") || normalizedKey.endsWith(".pdf");
    }

    private boolean isImage(String contentType, String key) {
        String normalizedContentType = contentType == null ? "" : contentType.toLowerCase(Locale.ROOT);
        String normalizedKey = key == null ? "" : key.toLowerCase(Locale.ROOT);

        return normalizedContentType.startsWith("image/jpeg")
                || normalizedContentType.startsWith("image/jpg")
                || normalizedContentType.startsWith("image/png")
                || normalizedKey.endsWith(".jpg")
                || normalizedKey.endsWith(".jpeg")
                || normalizedKey.endsWith(".png");
    }

    private String defaultString(String value) {
        if (value == null || value.isBlank()) {
            return "-";
        }
        return value;
    }
}
