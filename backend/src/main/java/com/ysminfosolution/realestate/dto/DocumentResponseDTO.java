package com.ysminfosolution.realestate.dto;

import java.util.UUID;

import com.ysminfosolution.realestate.model.Document.DocumentType;

public record DocumentResponseDTO(
    UUID documentId,
    String documentTitle,
    DocumentType documentType,
    String documentURL
) {

}
