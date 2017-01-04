Written in Eclipse IDE and Android Studio
A text generator for a Kanye Ipsum app (based off Lorem Ipsum) I am working on. Android Studio portion will be pushed soon once I've made decent progress on it.

This text generator takes in a txt file (in the local directory), each line being a paragraph. The "speech.txt" file is pre-loaded with Kanye speeches and lyrics. Meant to be used in my app.

The next directory is my Android Studio work. This is the actual work and will be updated on a consistent basis. Comments are kept for my own reference.

So far it supports the following features:
	- Generating a single paragraph with N sentences
	- Generating N paragraphs
	- Each are randomized in their selections
	- Supports both big and small N (repetitions will start if N is greater than the amount of sentences/paragraphs I've prepared)
	- Censorship support, removes cursing
	- Paragraph tags (HTML)
	- All caps feature
	- Clear feature (AS)
	- Interactivity with the censor function (AS)
	- Share button
	- Limitation on amount of characters to input
	- Switch between sentences/paragraphs


Further implementation:
	- Possibly header tags
	- Randomly adding ? or !, possibly ignoring lines that end with "..."
	- Support for classic Lorem
	- Exporting to docx and txt
	

Things learned:
	- More familiar with regular-expression constructs in Java like word boundaries

	- Use of threads in Android Studio
	- Interactivity between different Widgets, like TextView, ScrollView, CheckBox, EditText, TextSwitcher
	- Incorporated a Splash screen in the "proper" way
	- Further familiarized myself with XML
	- Handling external/internal storage
	- More understanding of API like DownloadManager, Uri


Known bugs:
	- Keyboard does not hide when clicking in the ScrollView area
	- (Fixing tomorrow) txt and doc support, figured how to convert File to Uri successfully
