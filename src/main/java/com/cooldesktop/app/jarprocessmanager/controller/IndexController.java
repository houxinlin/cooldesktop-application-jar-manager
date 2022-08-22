package com.cooldesktop.app.jarprocessmanager.controller;

import com.cooldesktop.app.jarprocessmanager.server.MemCodeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {
   private final MemCodeService codeService;
    public IndexController(MemCodeService codeService) {
        this.codeService = codeService;
    }

    /**
    * @description: 首页
    * @date: 2022/8/22 上午10:30
    */

    @GetMapping("/")
    private String index() {
        return "index";
    }

    /**
    * @description: 仪表盘
    * @date: 2022/8/22 上午10:30
    */

    @GetMapping("dashboard")
    private String dashboard(@RequestParam("jid")String jid, Model model) {
        model.addAttribute("jid",jid);
        return "dashboard";
    }

    /**
    * @description: 代码展示
    * @date: 2022/8/22 上午10:30
    */

    @GetMapping("code")
    private String code(@RequestParam("perviewId")String perviewId, Model model) {
        model.addAttribute("code",codeService.getCode(perviewId));
        return "code-perview";
    }
}
