package com.ecommerce.user.service;

import com.ecommerce.user.dto.AddressDTO;
import com.ecommerce.user.dto.UserDTO;
import com.ecommerce.user.entity.Address;
import com.ecommerce.user.entity.User;
import com.ecommerce.user.repository.AddressRepository;
import com.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    public UserDTO getCurrentUserProfile() {
        User user = getCurrentUser();
        return mapToUserDTO(user);
    }

    @Transactional
    public UserDTO updateProfile(UserDTO userDTO) {
        User user = getCurrentUser();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPhone(userDTO.getPhone());
        userRepository.save(user);
        return mapToUserDTO(user);
    }

    public List<AddressDTO> getUserAddresses() {
        User user = getCurrentUser();
        return addressRepository.findByUserId(user.getId()).stream()
                .map(this::mapToAddressDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AddressDTO addAddress(AddressDTO addressDTO) {
        User user = getCurrentUser();
        Address address = Address.builder()
                .user(user)
                .addressType(addressDTO.getAddressType())
                .streetAddress(addressDTO.getStreetAddress())
                .city(addressDTO.getCity())
                .state(addressDTO.getState())
                .postalCode(addressDTO.getPostalCode())
                .country(addressDTO.getCountry())
                .isDefault(addressDTO.isDefault())
                .build();

        if (addressDTO.isDefault()) {
            resetDefaultAddresses(user.getId());
        }

        Address savedAddress = addressRepository.save(address);
        return mapToAddressDTO(savedAddress);
    }

    private void resetDefaultAddresses(UUID userId) {
        List<Address> addresses = addressRepository.findByUserId(userId);
        addresses.forEach(a -> a.setDefault(false));
        addressRepository.saveAll(addresses);
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private UserDTO mapToUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .build();
    }

    private AddressDTO mapToAddressDTO(Address address) {
        return AddressDTO.builder()
                .id(address.getId())
                .addressType(address.getAddressType())
                .streetAddress(address.getStreetAddress())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .isDefault(address.isDefault())
                .build();
    }
}
