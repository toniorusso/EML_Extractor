# EML_Extractor

This is a simple java project that allows you to extract certain values ​​from an input .eml file and write them to an output .txt file. At present with this EmlExtractor can get information about Sender, Subject and Body. EmlExtractor is able to decode the body of emails in base64 and html and convert it into plain text.

## Instructions
To use the EML_Extrator just download the jar file in the *target* folder and use the following command from the cli:

```
java -jar <filename>.jar "<inputfile-path\\filename.eml>" "<outputfile-path\\filename.txt>"
```

**Important: Make sure the command is run in the directory where the jar is located on your computer**
