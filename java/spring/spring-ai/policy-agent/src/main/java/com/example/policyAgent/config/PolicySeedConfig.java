package com.example.policyAgent.config;

import com.mongodb.client.MongoClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class PolicySeedConfig implements ApplicationRunner {

	@Value("${spring.mongodb.database}")
	private String database;

	@Value("${spring.ai.vectorstore.mongodb.collection-name}")
	private String collection;

	private final VectorStore vectorStore;

	private final MongoClient mongoClient;

	public PolicySeedConfig(VectorStore vectorStore, MongoClient mongoClient) {
		this.vectorStore = vectorStore;
		this.mongoClient = mongoClient;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {

		if (mongoClient.getDatabase(database).getCollection(collection).countDocuments() > 0) {
			return;
		}

		var listPolicy = List.of(
				new Document(
						"Employees must complete at least one full year, or 365 consecutive days, "
								+ "of employment before becoming eligible to take vacation leave.",
						Map.of(
								"title", "Vacation Eligibility",
								"category", "vacation"
						)
				),

				new Document(
						"To request reimbursement for a business expense, employees must submit "
								+ "the corresponding invoice or receipt as proof of purchase.",
						Map.of(
								"title", "Expense Reimbursement",
								"category", "reimbursement"
						)
				),

				new Document(
						"After completing three years of employment, employees are eligible to receive "
								+ "a new company laptop. They may keep their previous laptop for personal use, "
								+ "subject to approval and completion of the required data removal process.",
						Map.of(
								"title", "Laptop Replacement",
								"category", "equipment"
						)
				),

				new Document(
						"Employees must not share confidential documents, internal information, source code, "
								+ "customer data, or company materials with anyone outside the company. "
								+ "Violations may result in disciplinary action, including termination of employment.",
						Map.of(
								"title", "Confidential Information",
								"category", "security"
						)
				),

				new Document(
						"All employees are entitled to company-sponsored health insurance according to "
								+ "the coverage options and eligibility rules established by the company.",
						Map.of(
								"title", "Health Insurance",
								"category", "benefits"
						)
				),

				new Document(
						"Company equity awards are distributed or vested quarterly according to "
								+ "the employee's grant agreement and vesting schedule.",
						Map.of(
								"title", "Company Equity Distribution",
								"category", "equity"
						)
				),

				new Document(
						"Employees may not sell company shares before the date established in their "
								+ "grant agreement or outside the trading periods authorized by the company.",
						Map.of(
								"title", "Equity Sale Restrictions",
								"category", "equity"
						)
				),

				new Document(
						"All employees are required to record their working hours using the company's "
								+ "official time-tracking system.",
						Map.of(
								"title", "Time Tracking",
								"category", "attendance"
						)
				),

				new Document(
						"If an employee fails to record their working hours, they must request a correction "
								+ "by email and provide a valid justification, such as business travel, "
								+ "system unavailability, or working from another country.",
						Map.of(
								"title", "Time Entry Corrections",
								"category", "attendance"
						)
				),

				new Document(
						"Employees working remotely must remain available during their agreed working hours "
								+ "and comply with the company's security, communication, and productivity requirements.",
						Map.of(
								"title", "Remote Work",
								"category", "remote-work"
						)
				),

				new Document(
						"Employees must obtain approval from their manager and the HR department "
								+ "before working remotely from another country.",
						Map.of(
								"title", "International Remote Work",
								"category", "remote-work"
						)
				),

				new Document(
						"Business travel expenses must comply with the company's travel policy. "
								+ "Employees must provide receipts and submit their reimbursement requests "
								+ "within 30 days after the trip.",
						Map.of(
								"title", "Business Travel Expenses",
								"category", "travel"
						)
				),

				new Document(
						"Company-provided equipment must be used responsibly and primarily for business purposes. "
								+ "Employees must immediately report any loss, theft, or damage.",
						Map.of(
								"title", "Company Equipment",
								"category", "equipment"
						)
				),

				new Document(
						"Employees must not share their passwords, authentication codes, API keys, "
								+ "or access credentials with other employees or external individuals.",
						Map.of(
								"title", "Password Security",
								"category", "security"
						)
				),

				new Document(
						"Employees must disclose any personal, professional, or financial situation "
								+ "that could create a conflict of interest with the company.",
						Map.of(
								"title", "Conflict of Interest",
								"category", "compliance"
						)
				),

				new Document(
						"Employees must maintain respectful and professional behavior in all workplace interactions. "
								+ "Harassment, discrimination, intimidation, and abusive conduct are not permitted.",
						Map.of(
								"title", "Workplace Conduct",
								"category", "conduct"
						)
				)
		);

		vectorStore.add(listPolicy);
	}

}
