package com.ysminfosolution.realestate.dto.enquiryPropertyOptions;

import com.ysminfosolution.realestate.model.Floor.PropertyType;

public interface PropertyOptionsFlat {
    PropertyType getPropertyType();
    String getProperty();
    Double getArea();
    Long getQuantity();
}
