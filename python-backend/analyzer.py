from textblob import TextBlob
import spacy
import math

nlp = spacy.load("en_core_web_sm")

def analyze_notes(text):
    blob = TextBlob(text)
    doc = nlp(text)

    # Summary (simple first 2 sentences)
    summary = ' '.join([str(s) for s in blob.sentences[:2]])

    # Keywords
    keywords = [token.text for token in doc if token.is_alpha and not token.is_stop]
    keywords = list(set([w.lower() for w in keywords]))[:10]

    # Sentiment
    polarity = blob.sentiment.polarity
    sentiment = "Positive" if polarity > 0.1 else "Negative" if polarity < -0.1 else "Neutral"

    # Word count and reading time
    word_count = len(text.split())
    reading_time = f"{math.ceil(word_count / 200)} min"

    return {
        "summary": summary,
        "keywords": keywords,
        "sentiment": sentiment,
        "word_count": word_count,
        "reading_time": reading_time
    }
