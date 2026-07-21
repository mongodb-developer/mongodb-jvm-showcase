package com.samples.growingthings.model;

import java.util.List;

public record GrowRecommendation(
		String recommendation,
		List<String> relatedTopics,
		List<String> keyPoints
) {}