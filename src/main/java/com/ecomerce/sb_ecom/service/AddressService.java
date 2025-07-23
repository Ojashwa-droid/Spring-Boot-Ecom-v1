package com.ecomerce.sb_ecom.service;


import com.ecomerce.sb_ecom.model.User;
import com.ecomerce.sb_ecom.payload.AddressDTO;
import com.ecomerce.sb_ecom.payload.AddressResponse;

import java.util.List;

public interface AddressService {

    AddressDTO createUserAddress(AddressDTO address, User user);

    AddressResponse getAllAddresses(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    AddressDTO getAddressById(Long addressId);

    List<AddressDTO> getUserAddresses(User user);

    AddressDTO updateAddress(Long addressId, AddressDTO addressDTO);

    String deleteAddress(Long addressId);
}
