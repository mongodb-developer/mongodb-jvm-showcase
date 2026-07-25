package com.example.showcase.web;

import com.example.showcase.service.ArticleService;
import org.bson.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/articles")
public class ArticleController {

	private final ArticleService articleService;

	public ArticleController(ArticleService articleService) {
		this.articleService = articleService;
	}

	@GetMapping("/search")
	public List<Document> search(@RequestParam("q") String q,
								 @RequestParam(name = "limit", defaultValue = "5") int limit) {

		return articleService.semanticSearch(q, limit);
	}

}
