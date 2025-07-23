package com.ecomerce.sb_ecom.service;

import com.ecomerce.sb_ecom.exceptions.APIException;
import com.ecomerce.sb_ecom.exceptions.ResourceNotFoundException;
import com.ecomerce.sb_ecom.model.Address;
import com.ecomerce.sb_ecom.model.User;
import com.ecomerce.sb_ecom.payload.AddressDTO;
import com.ecomerce.sb_ecom.payload.AddressResponse;
import com.ecomerce.sb_ecom.repositories.AddressRepository;
import com.ecomerce.sb_ecom.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public AddressDTO createUserAddress(AddressDTO addressDTO, User user) { // accepts an addressDTO and creates a new address against a logged-in user
       Address address = modelMapper.map(addressDTO, Address.class);

       List<Address> addressList = user.getAddresses();
       addressList.add(address);
       user.setAddresses(addressList);

       address.setUser(user);
       Address savedAddress = addressRepository.save(address);

        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public AddressResponse getAllAddresses(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Address> addressPage = addressRepository.findAll(pageDetails);

        List<Address> addressList = addressPage.getContent();
        if (addressList.isEmpty()){
            throw new APIException("No Addresses Found");
        }

        List<AddressDTO> addressDTOList = addressList.stream()
                .map(address -> {
                    AddressDTO addressDTO = modelMapper.map(address, AddressDTO.class);
                    return addressDTO;
                }).collect(Collectors.toList());

        AddressResponse addressResponse = new AddressResponse();
        addressResponse.setContent(addressDTOList);
        addressResponse.setTotalElements(addressPage.getTotalElements());
        addressResponse.setLastPage(addressPage.isLast());
        addressResponse.setPageNumber(addressPage.getNumber());
        addressResponse.setPageSize(addressPage.getSize());

        return addressResponse;
    }

    @Override
    public AddressDTO getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(()-> new ResourceNotFoundException("Address", "addressId", addressId));
        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getUserAddresses(User user) {

//        List<Address> addressList = user.getAddresses(); /* We can make use of this way as well. */

        List<Address> addressList = addressRepository.findByUser(user);
        if (addressList.isEmpty()){
            throw new APIException("No Address Found for username " + user.getUserName());
        }
        return addressList.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class)).toList();
    }

    @Override
    public AddressDTO updateAddress(Long addressId, AddressDTO addressDTO) {
        Address addressFromDatabase = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

        addressFromDatabase.setCity(addressDTO.getCity());
        addressFromDatabase.setPincode(addressDTO.getPincode());
        addressFromDatabase.setStreet(addressDTO.getStreet());
        addressFromDatabase.setCountry(addressDTO.getCountry());
        addressFromDatabase.setBuildingName(addressDTO.getBuildingName());

        Address updatedAddress = addressRepository.save(addressFromDatabase);

        User user = addressFromDatabase.getUser();
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        user.getAddresses().add(updatedAddress);
        userRepository.save(user);

        return modelMapper.map(updatedAddress, AddressDTO.class);
    }

    @Transactional
    @Override
    public String deleteAddress(Long addressId) {
        Address addressFromDatabase = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));
        User user = addressFromDatabase.getUser();
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressFromDatabase.getAddressId()));
        userRepository.save(user);

        addressRepository.delete(addressFromDatabase);

        return "Address deleted successfully with addressId:  " + addressId;
    }
}