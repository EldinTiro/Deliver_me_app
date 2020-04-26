package com.StoreApp.WS.userservice;

import com.StoreApp.WS.shared.dto.AddressDTO;

import java.util.List;

public interface IAddressService {
    List<AddressDTO> getAddresses(String userId);
    AddressDTO getAddress(String addressId);
}