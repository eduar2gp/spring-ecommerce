package com.ecommerce.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class AllDevicesNotificationRequestDTO extends NotificationRequestDTO {

    List<String> deviceTokenList = new ArrayList<>();

    public List<String> getDeviceTokenList() {
        return deviceTokenList;
    }

    public void setDeviceTokenList(List<String> deviceTokenList) {
        this.deviceTokenList = deviceTokenList;
    }

}
