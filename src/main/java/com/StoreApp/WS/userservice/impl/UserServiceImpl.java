package com.StoreApp.WS.userservice.impl;

import com.StoreApp.WS.io.entity.UserEntity;
import com.StoreApp.WS.io.repositiories.UserRepository;
import com.StoreApp.WS.shared.Utils;
import com.StoreApp.WS.shared.dto.AddressDTO;
import com.StoreApp.WS.shared.dto.UserDto;
import com.StoreApp.WS.ui.model.response.ErrorMessages;
import com.StoreApp.WS.userservice.IUserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    Utils utils;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDto createUser(UserDto user) {

        if(userRepository.findByEmail(user.getEmail())!=null) throw new RuntimeException("Record already exists");

        for (int i = 0; i < user.getAddresses().size(); i++) {
            AddressDTO address = user.getAddresses().get(i);
            address.setUserDetails(user);
            address.setAddressId(utils.generateAddressId(30));
            user.getAddresses().set(i,address);

        }
        //BeanUtils.copyProperties(user,userEntity);
        ModelMapper modelMapper = new ModelMapper();
        UserEntity userEntity = modelMapper.map(user,UserEntity.class);


        String publicUserId = utils.generateUserId(30);
        userEntity.setUserId(publicUserId);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));


        UserEntity storedUserDetails = userRepository.save(userEntity);

        //BeanUtils.copyProperties(storedUserDetails, returnValue);
        UserDto returnValue = modelMapper.map(storedUserDetails, UserDto.class);

        return returnValue;
    }

    @Override
    public UserDto getUser(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null) throw new UsernameNotFoundException(email);

        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(userEntity, returnValue);
        return returnValue;
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserDto returnValue = new UserDto();
        UserEntity userEntity = userRepository.findByUserId(userId);
        if (userEntity == null)
            throw new UsernameNotFoundException(userId);

        BeanUtils.copyProperties(userEntity, returnValue);

        return returnValue;
    }

    @Override
    public UserDto updateUser(String id, UserDto user) {
        UserDto returnValue = new UserDto();
        UserEntity userEntity = userRepository.findByUserId(id);
        if (userEntity == null)
            throw new UsernameNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());

        UserEntity updatedUserDetails = userRepository.save(userEntity);

        BeanUtils.copyProperties(updatedUserDetails,returnValue);

        return returnValue;

    }

    @Override
    public void deleteUser(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null)
            throw new UsernameNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        userRepository.delete(userEntity);
    }

    @Override
    //Service za paginaciju, u klasu UserRepository se je izmjenilo da extenda novu klasu "PagingAndSortingRepository"
    //koja podrzava i ostale metode kao sto su findByEmail, findByUserId itd. Samo sada je moguce zatraziti iz baze response preko nove metode
    //"findAll() koja prima Pageable kao request.
    public List<UserDto> getUsers(int page, int limit) {
        List<UserDto> returnValue = new ArrayList<>();

        if(page > 0) page = -1; //cisto zbog indeksiranja paginacije, jer page pocinje od 0, pa da ne dodje do zabune sa strane user-a

        Pageable pageableRequest = PageRequest.of(page, limit);

        Page<UserEntity> usersPage = userRepository.findAll(pageableRequest);
        List<UserEntity> users = usersPage.getContent();

        for (UserEntity userEntity : users) {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(userEntity,userDto);
            returnValue.add(userDto);

        }

        return returnValue;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null) throw new UsernameNotFoundException(email);
        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
    }
}
