package com.devrel.wms.limits;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

	private static final Set<String> LIMITED_METHODS = Set.of("POST", "PUT", "PATCH", "DELETE");
	private static final int MAX_TRACKED_CLIENTS = 10_000;

	private final DemoLimits limits;
	private final Map<String, Bucket> bucketsByClient = new ConcurrentHashMap<>();
	private final Bucket globalBucket;

	RateLimitFilter(DemoLimits limits) {
		this.limits = limits;
		this.globalBucket = Bucket.builder()
				.addLimit(Bandwidth.classic(
						limits.globalRequestsPerDay(),
						Refill.intervally(limits.globalRequestsPerDay(), Duration.ofDays(1))))
				.build();
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		if (!limits.enabled()) {
			return true;
		}

		return !LIMITED_METHODS.contains(request.getMethod()) && !isCostlyRead(request);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		if (!clientBucket(request).tryConsume(1)) {
			reject(response, "Too many requests from your address. Please wait a moment.");

			return;
		}

		if (!globalBucket.tryConsume(1)) {
			reject(response, "This showcase reached its daily usage limit. Please try again tomorrow.");

			return;
		}

		chain.doFilter(request, response);
	}

	private boolean isCostlyRead(HttpServletRequest request) {
		return request.getRequestURI().startsWith("/depositor-policies/")
				&& request.getRequestURI().endsWith("/preview");
	}

	private Bucket clientBucket(HttpServletRequest request) {
		if (bucketsByClient.size() >= MAX_TRACKED_CLIENTS) {
			bucketsByClient.clear();
		}

		return bucketsByClient.computeIfAbsent(clientKey(request), key -> Bucket.builder()
				.addLimit(Bandwidth.classic(
						limits.perIpRequestsPerMinute(),
						Refill.greedy(limits.perIpRequestsPerMinute(), Duration.ofMinutes(1))))
				.build());
	}

	private String clientKey(HttpServletRequest request) {
		String forwardedFor = request.getHeader("X-Forwarded-For");

		if (StringUtils.hasText(forwardedFor)) {
			return forwardedFor.split(",")[0].trim();
		}

		return request.getRemoteAddr();
	}

	private void reject(HttpServletResponse response, String message) throws IOException {
		response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.getWriter().write("{\"message\":\"%s\"}".formatted(message));
	}
}
