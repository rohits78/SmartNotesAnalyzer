from flask import Flask, request, jsonify
from flask_cors import CORS
from analyzer import analyze_notes

app = Flask(__name__)
CORS(app)

@app.route("/analyze", methods=["POST"])
def analyze():
    data = request.get_json()
    text = data.get("text", "")

    if not text.strip():
        return jsonify({"error": "Empty input"}), 400

    result = analyze_notes(text)
    return jsonify(result)

if __name__ == "__main__":
    app.run(port=5000)
