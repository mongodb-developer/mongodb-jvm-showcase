package com.mongodb.domain;

import jakarta.persistence.Embeddable;
import org.hibernate.annotations.Struct;

@Embeddable
@Struct(name = "RecentReview")
public record RecentReview (
      String author,
      String comment)
{}
