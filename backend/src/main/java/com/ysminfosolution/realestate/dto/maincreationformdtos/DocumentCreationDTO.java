package com.ysminfosolution.realestate.dto.maincreationformdtos;

import org.springframework.web.multipart.MultipartFile;

import com.ysminfosolution.realestate.model.Document.DocumentType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record DocumentCreationDTO(

    @NotNull(message = "Document type is required")
    DocumentType documentType,

    @NotBlank(message = "Document title is required")
    @Pattern(regexp = "^^[^/]*$", message = "Document title must not contain '/'")
    String documentTitle,

    @NotNull(message = "Document is required")
    MultipartFile document
) {}
