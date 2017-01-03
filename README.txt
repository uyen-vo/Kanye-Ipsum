Written in Eclipse IDE
A text generator for a Kanye Ipsum app (based off Lorem Ipsum) I am working on. Android Studio portion will be pushed soon once I've made decent progress on it.

This text generator takes in a txt file (in the local directory), each line being a paragraph. The "speech.txt" file is pre-loaded with Kanye speeches and lyrics. Meant to be used in my app.

So far it supports the following features:
	- Generating a single paragraph with N sentences
	- Generating N paragraphs
	- Each are randomized in their selections
	- Supports both big and small N (repetitions will start if N is greater than the amount of sentences/paragraphs I've prepared)
	- Censorship support, removes cursing
	- Paragraph tags (HTML)
	- All caps feature

Further implementation:
	- Possibly header tags
	- Randomly adding ? or !, possibly ignoring lines that end with "..."
	- Most of the other functions the app will support will probably be done in Android Studio (exporting to .docx or .txt)
	- Support for classic Lorem

Things learned:
	- More familiar with regular-expression constructs in Java like word boundaries