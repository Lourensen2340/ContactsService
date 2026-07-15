package com.yaskondrichin.ContactsService.Utils;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("utilsPasswordGenerator")
public class PasswordGenerator {
 public String generateRandomPassword(){
     return UUID.randomUUID().toString()
             .replace("-","")
             .substring(0, 12);
 }
}
