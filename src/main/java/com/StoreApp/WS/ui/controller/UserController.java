package com.StoreApp.WS.ui.controller;

import com.StoreApp.WS.shared.dto.AddressDTO;
import com.StoreApp.WS.shared.dto.UserDto;
import com.StoreApp.WS.ui.model.request.UserDetailsRequestModel;
import com.StoreApp.WS.ui.model.response.*;
import com.StoreApp.WS.userservice.IAddressService;
import com.StoreApp.WS.userservice.IUserService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/users") //http://localhost:8080/users
public class UserController {

    Map<String, UserRest> users;

    @Autowired
    IUserService userService;

    @Autowired
    IAddressService addressService;

    @Autowired
    IAddressService addressesService;

    @GetMapping(path = "/{id}",
            produces = {MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_JSON_VALUE}
    )
    public UserRest getUser(@PathVariable String id) {

        UserRest returnValue = new UserRest();
        UserDto userDto = userService.getUserByUserId(id);

        ModelMapper modelMapper = new ModelMapper();
        returnValue = modelMapper.map(userDto,UserRest.class);

        return returnValue;
    }

    @PostMapping(consumes = {
            MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_JSON_VALUE
    },
            produces = {
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_JSON_VALUE
            })
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) {

        UserRest returnedValue = new UserRest();
        if(userDetails.getFirstName().isEmpty()) throw new NullPointerException("The object is null");

        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetails,UserDto.class);

        UserDto createdUser = userService.createUser(userDto);
        returnedValue = modelMapper.map(createdUser,UserRest.class);

        return returnedValue;
    }

    @PutMapping(path = "/{userId}",
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
            )
    public UserRest updateUser(@PathVariable String userId, @RequestBody UserDetailsRequestModel userDetails) {
        UserRest returnedValue = new UserRest();
        if(userDetails.getFirstName().isEmpty()) throw new NullPointerException("The object is null");

        UserDto userDto = new UserDto();

        BeanUtils.copyProperties(userDetails,userDto);

        UserDto updatedUser = userService.updateUser(userId,userDto);
        BeanUtils.copyProperties(updatedUser,returnedValue);

        return returnedValue;
    }

    @DeleteMapping(path = "/{userId}")
    public OperationStatusModel deleteUser(@PathVariable String userId) {
        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.DELETE.name());

        userService.deleteUser(userId);

        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return returnValue;

    }

    @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public List<UserRest> getUsers(@RequestParam(value="page", defaultValue = "0")int page,
                                   @RequestParam(value="limit", defaultValue = "2")int limit){

        List<UserRest> returnValue = new ArrayList<>();

        List<UserDto> users = userService.getUsers(page,limit);

        for (UserDto userDto : users) {
            UserRest userModel = new UserRest();
            BeanUtils.copyProperties(userDto, userModel);
            returnValue.add(userModel);
        }

        return returnValue;
    }
    // http://localhost:8080/photoapp/users/s76df56/addresses
    @GetMapping(path = "/{userId}/addresses",
            produces = {MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_JSON_VALUE}
    )
    public List<AddressesRest> getUserAddresses(@PathVariable String userId) {

        List<AddressesRest> addressesListRestModel = new ArrayList<>();
        List<AddressDTO> addressDTOS = addressesService.getAddresses(userId);

        if(addressDTOS !=null && !addressDTOS.isEmpty()) {
            Type listType = new TypeToken<List<AddressesRest>>() {}.getType();
            addressesListRestModel = new ModelMapper().map(addressDTOS, listType);
        }
        for (AddressesRest addressRest: addressesListRestModel) {
            Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(userId,addressRest.getAddressId())).withSelfRel();
            addressRest.add(addressLink);

            Link userLink = linkTo(methodOn(UserController.class).getUser(userId)).withRel("user");
            addressRest.add(userLink);
        }

        return addressesListRestModel;
    }
    // http://localhost:8080/photoapp/users/s76df56/addresses
    @GetMapping(path = "/{userId}/addresses/{addressId}",
            produces = {MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_JSON_VALUE}
    )
    public AddressesRest getUserAddress(@PathVariable String userId, @PathVariable String addressId) {

        AddressDTO addressDTOS = addressService.getAddress(addressId);
        ModelMapper modelMapper = new ModelMapper();

        Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(userId,addressId)).withSelfRel();
        Link addressesLink = linkTo(methodOn(UserController.class).getUserAddresses(userId)).withRel("addresses");
        Link userLink = linkTo(methodOn(UserController.class).getUser(userId)).withRel("user");

        AddressesRest addressesRest = modelMapper.map(addressDTOS,AddressesRest.class);
        addressesRest.add(addressLink);
        addressesRest.add(addressesLink);
        addressesRest.add(userLink);

        return addressesRest;
    }

}
