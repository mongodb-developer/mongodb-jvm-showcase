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
						"Employees become eligible to take vacation leave after completing "
								+ "365 consecutive days of employment.",
						Map.of(
								"title", "Vacation Eligibility",
								"category", "vacation"
						)
				),

				new Document(
						"Employees requesting reimbursement for approved business purchases must provide "
								+ "a valid invoice or receipt and submit the request through the official expense system.",
						Map.of(
								"title", "General Expense Reimbursement",
								"category", "reimbursement"
						)
				),

				new Document(
						"Employees become eligible for a replacement company laptop after completing "
								+ "three years of employment. Keeping the previous device requires approval "
								+ "and completion of the company's data removal process.",
						Map.of(
								"title", "Laptop Replacement",
								"category", "equipment"
						)
				),

				new Document(
						"Employees must not disclose confidential documents, internal business information, "
								+ "source code, customer information, or proprietary company materials "
								+ "to unauthorized individuals.",
						Map.of(
								"title", "Confidential Information",
								"category", "security"
						)
				),

				new Document(
						"Eligible employees may participate in the company-sponsored health insurance plan "
								+ "according to the available coverage options and enrollment rules.",
						Map.of(
								"title", "Health Insurance",
								"category", "benefits"
						)
				),

				new Document(
						"Company equity awards vest quarterly according to the dates, percentages, "
								+ "and conditions defined in each employee's grant agreement.",
						Map.of(
								"title", "Equity Vesting",
								"category", "equity"
						)
				),

				new Document(
						"Employees may only sell company shares after they have vested and during trading "
								+ "periods officially authorized by the company.",
						Map.of(
								"title", "Equity Trading Restrictions",
								"category", "equity"
						)
				),

				new Document(
						"Employees must accurately record their working hours using the company's "
								+ "official time-tracking system.",
						Map.of(
								"title", "Time Tracking",
								"category", "attendance"
						)
				),

				new Document(
						"Employees who forget or are unable to record their working hours must request "
								+ "a correction by email and provide a valid explanation for the missing entry.",
						Map.of(
								"title", "Time Entry Corrections",
								"category", "attendance"
						)
				),

				new Document(
						"Employees working remotely must remain available during their agreed working hours "
								+ "and follow the company's communication and productivity expectations.",
						Map.of(
								"title", "Remote Work Availability",
								"category", "remote-work"
						)
				),

				new Document(
						"Employees must receive approval from both their manager and the HR department "
								+ "before temporarily working from another country.",
						Map.of(
								"title", "International Remote Work",
								"category", "remote-work"
						)
				),

				new Document(
						"Expenses related to approved business travel must follow the company's travel policy "
								+ "and be submitted with receipts within 30 days after the trip.",
						Map.of(
								"title", "Business Travel Reimbursement",
								"category", "travel"
						)
				),

				new Document(
						"Company-provided devices and accessories must be handled responsibly. Employees must "
								+ "immediately report any loss, theft, malfunction, or physical damage.",
						Map.of(
								"title", "Company Equipment Care",
								"category", "equipment"
						)
				),

				new Document(
						"Employees must keep passwords, authentication codes, API keys, tokens, "
								+ "and other access credentials private and secure.",
						Map.of(
								"title", "Credential Security",
								"category", "security"
						)
				),

				new Document(
						"Employees must disclose any personal, professional, or financial interest "
								+ "that could interfere with their responsibilities or company decisions.",
						Map.of(
								"title", "Conflict of Interest",
								"category", "compliance"
						)
				),

				new Document(
						"Employees must treat colleagues, customers, and partners respectfully. "
								+ "Harassment, discrimination, intimidation, and abusive behavior are prohibited.",
						Map.of(
								"title", "Workplace Conduct",
								"category", "conduct"
						)
				),

				new Document(
						"Employees who are unable to work because of illness must notify their manager "
								+ "as soon as possible. Supporting medical documentation may be required.",
						Map.of(
								"title", "Sick Leave",
								"category", "leave"
						)
				),

				new Document(
						"Eligible employees may take parental leave following the birth, adoption, "
								+ "or legal placement of a child, according to company policy and local law.",
						Map.of(
								"title", "Parental Leave",
								"category", "leave"
						)
				),

				new Document(
						"Employees may request bereavement leave following the death of an immediate "
								+ "family member and must notify their manager and HR.",
						Map.of(
								"title", "Bereavement Leave",
								"category", "leave"
						)
				),

				new Document(
						"Employees must receive approval from their manager before working overtime. "
								+ "Approved overtime must be recorded separately in the time-tracking system.",
						Map.of(
								"title", "Overtime Authorization",
								"category", "attendance"
						)
				),

				new Document(
						"Employees may request changes to their usual start and end times with manager approval, "
								+ "provided the schedule continues to support business and team requirements.",
						Map.of(
								"title", "Flexible Work Schedule",
								"category", "work-arrangement"
						)
				),

				new Document(
						"Employees will participate in periodic performance reviews to discuss results, "
								+ "objectives, feedback, expectations, and professional development opportunities.",
						Map.of(
								"title", "Performance Reviews",
								"category", "performance"
						)
				),

				new Document(
						"Employees must complete assigned security, compliance, privacy, and workplace training "
								+ "courses before their respective deadlines.",
						Map.of(
								"title", "Mandatory Training",
								"category", "training"
						)
				),

				new Document(
						"Personal data may only be collected, accessed, processed, or shared for authorized "
								+ "business purposes and in accordance with applicable privacy requirements.",
						Map.of(
								"title", "Personal Data Protection",
								"category", "privacy"
						)
				),

				new Document(
						"Business records must be stored for the period defined in the company's retention schedule. "
								+ "Records subject to legal or compliance requirements must not be altered or deleted.",
						Map.of(
								"title", "Records Retention",
								"category", "compliance"
						)
				),

				new Document(
						"Company email, internet access, messaging platforms, software, and devices must be used "
								+ "for legitimate and authorized activities.",
						Map.of(
								"title", "Acceptable Use of Technology",
								"category", "technology"
						)
				),

				new Document(
						"Employees must not publish internal company matters on social media or present personal "
								+ "opinions as official statements made on behalf of the company.",
						Map.of(
								"title", "Social Media Communication",
								"category", "communication"
						)
				),

				new Document(
						"Employees must not accept gifts, entertainment, or hospitality that could influence "
								+ "a business decision. Items above the permitted value must be reported.",
						Map.of(
								"title", "Gifts and Hospitality",
								"category", "compliance"
						)
				),

				new Document(
						"Employees must not offer, request, authorize, or accept bribes, kickbacks, "
								+ "facilitation payments, or other improper financial benefits.",
						Map.of(
								"title", "Anti-Bribery",
								"category", "compliance"
						)
				),

				new Document(
						"Employees may report suspected fraud, misconduct, unethical behavior, or policy violations "
								+ "through approved reporting channels without retaliation for good-faith reports.",
						Map.of(
								"title", "Whistleblower Protection",
								"category", "compliance"
						)
				),

				new Document(
						"Employees responsible for selecting suppliers must follow the approved procurement process "
								+ "and evaluate vendors based on objective business, cost, quality, security, "
								+ "and compliance requirements.",
						Map.of(
								"title", "Vendor Selection",
								"category", "procurement"
						)
				),

				new Document(
						"When employment ends, employees must return company property, transfer relevant work, "
								+ "remove company information from authorized personal devices, and complete "
								+ "all required access-revocation procedures.",
						Map.of(
								"title", "Employee Offboarding",
								"category", "offboarding"
						)
				)
		);

		vectorStore.add(listPolicy);
	}

}
