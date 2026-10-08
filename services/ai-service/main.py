import re

from fastapi import FastAPI
from pydantic import BaseModel, Field

app = FastAPI(title="TriageDesk AI Service")


class TriageRequest(BaseModel):
    title: str = Field(min_length=1, max_length=200)
    description: str = Field(min_length=1)


class TriageResponse(BaseModel):
    priority: str
    category: str
    suggested_reply: str


# Words that push a ticket to a priority level (checked from most urgent down)
PRIORITY_RULES = [
    ("URGENT", ["outage", "down", "data loss", "hacked", "breach", "production", "urgent"]),
    ("HIGH", ["cannot", "can't", "unable", "error", "crash", "failed", "not working", "broken", "never"]),
    ("LOW", ["question", "how to", "suggestion", "feature", "typo", "would like"]),
]

# Words that point to a category; the category with the most matches wins
CATEGORY_RULES = {
    "ACCOUNT": ["login", "log in", "password", "account", "sign in", "email", "access"],
    "BILLING": ["invoice", "payment", "refund", "charge", "billing", "subscription"],
    "BUG": ["error", "crash", "exception", "bug", "broken", "not working", "failed"],
    "FEATURE_REQUEST": ["feature", "suggestion", "would like", "add support", "improve"],
}

REPLIES = {
    "ACCOUNT": "Thanks for reaching out. We're looking into your account access issue and will get back to you shortly.",
    "BILLING": "Thanks for contacting us. Our billing team is reviewing your request and will follow up soon.",
    "BUG": "Thanks for the report. Our team is investigating the problem and will update you as soon as we know more.",
    "FEATURE_REQUEST": "Thanks for the suggestion! We've passed it to our product team for consideration.",
    "GENERAL": "Thanks for your message. An agent will review your ticket and reply soon.",
}


def contains(text: str, keyword: str) -> bool:
    # \b means "whole word", so "down" does not match "download"
    return re.search(r"\b" + re.escape(keyword) + r"\b", text) is not None


def pick_priority(text: str) -> str:
    for level, keywords in PRIORITY_RULES:
        if any(contains(text, k) for k in keywords):
            return level
    return "MEDIUM"


def pick_category(text: str) -> str:
    scores = {
        name: sum(1 for k in keywords if contains(text, k))
        for name, keywords in CATEGORY_RULES.items()
    }
    best = max(scores, key=scores.get)
    return best if scores[best] > 0 else "GENERAL"


@app.get("/health")
def health():
    return {"status": "ok"}


@app.post("/triage", response_model=TriageResponse)
def triage(request: TriageRequest):
    text = f"{request.title} {request.description}".lower()
    category = pick_category(text)
    return TriageResponse(
        priority=pick_priority(text),
        category=category,
        suggested_reply=REPLIES[category],
    )