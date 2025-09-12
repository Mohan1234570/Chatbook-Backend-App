package in.krish.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class CommentController {

	@GetMapping("/comment")
	public String Comment() {
		
		//model.addAttribute("comment", new comment());
		return "comment";
	}
	
}
