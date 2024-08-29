package com.yolo.customer.userProfile;

import com.yolo.customer.address.Address;
import com.yolo.customer.address.AddressRepository;
import com.yolo.customer.currency.Currency;
import com.yolo.customer.currency.CurrencyRepository;
import com.yolo.customer.user.User;
import com.yolo.customer.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private UserRepository userRepository;

    public UserProfile createUserProfile(String username, UserProfileRequestDTO userProfileRequest) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Currency currency = currencyRepository.findByCode(userProfileRequest.getCurrency_code())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Currency not found"));

        Address address = new Address();
        address.setHouse(userProfileRequest.getHouse());
        address.setStreet(userProfileRequest.getStreet());
        address.setArea(userProfileRequest.getArea());
        address.setZipCode(userProfileRequest.getZip_code());
        address.setCity(userProfileRequest.getCity());
        address.setCountry(userProfileRequest.getCountry());
        address = addressRepository.save(address);

        UserProfile userProfile = new UserProfile();
        userProfile.setFirstName(userProfileRequest.getFirst_name());
        userProfile.setLastName(userProfileRequest.getLast_name());
        userProfile.setContactNumber(userProfileRequest.getContact_number());
        userProfile.setUserId(user.getId());
        userProfile.setCurrencyId(currency.getId());
        userProfile.setAddressId(address.getId());
        userProfile = userProfileRepository.save(userProfile);

        return userProfile;
    }

    @Transactional
    public void updateUserProfile(String username, UpdateUserProfileDTO userProfileUpdateRequest) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        UserProfile existingProfile = userProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found"));

        if (userProfileUpdateRequest.getContact_number() != null) {
            existingProfile.setContactNumber(userProfileUpdateRequest.getContact_number());
        }

        if (userProfileUpdateRequest.getHouse() != null || userProfileUpdateRequest.getStreet() != null ||
                userProfileUpdateRequest.getArea() != null || userProfileUpdateRequest.getZip_code() != null ||
                userProfileUpdateRequest.getCity() != null || userProfileUpdateRequest.getCountry() != null) {

            Address address = addressRepository.findById(existingProfile.getAddressId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));

            if (userProfileUpdateRequest.getHouse() != null) {
                address.setHouse(userProfileUpdateRequest.getHouse());
            }
            if (userProfileUpdateRequest.getStreet() != null) {
                address.setStreet(userProfileUpdateRequest.getStreet());
            }
            if (userProfileUpdateRequest.getArea() != null) {
                address.setArea(userProfileUpdateRequest.getArea());
            }
            if (userProfileUpdateRequest.getZip_code() != null) {
                address.setZipCode(userProfileUpdateRequest.getZip_code());
            }
            if (userProfileUpdateRequest.getCity() != null) {
                address.setCity(userProfileUpdateRequest.getCity());
            }
            if (userProfileUpdateRequest.getCountry() != null) {
                address.setCountry(userProfileUpdateRequest.getCountry());
            }
            addressRepository.save(address);
        }

        if (userProfileUpdateRequest.getCurrency_code() != null) {
            Currency currency = currencyRepository.findByCode(userProfileUpdateRequest.getCurrency_code())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Currency not found"));
            existingProfile.setCurrencyId(currency.getId());
        }

        userProfileRepository.save(existingProfile);
    }


}
