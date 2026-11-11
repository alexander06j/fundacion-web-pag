package com.fundacionunidosporpinias.fundacion_web.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(){
        return "index";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }


    // Sin método HTTP fijo: Spring Security reenvía aquí tanto GET como POST denegados
    @RequestMapping("/acceso-denegado")
    public String accesoDenegado(){
        return "acceso-denegado";
    }


}
