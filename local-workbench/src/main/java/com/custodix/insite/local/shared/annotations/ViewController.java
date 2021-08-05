package com.custodix.insite.local.shared.annotations;

import java.lang.annotation.*;

import org.springframework.stereotype.Controller;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Controller
public @interface ViewController {
}
