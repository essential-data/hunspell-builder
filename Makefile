# yes, having maven _and_ Makefile is ugly. I still prefer it for now.

all: dictionaries

build: target/hunspell-builder-0.2-jar-with-dependencies.jar

target/hunspell-builder-0.2-jar-with-dependencies.jar:
	mvn compile package

clean:
	mvn clean
	rm -fr dictionaries morph-sk.txt # hunspell-sk-20110228 hunspell-sk-20110228.zip

morph-sk.txt:
	wget -O - 'http://korpus.sk/attachments/morphology_database/ma-2015-02-05.txt.xz' | xzcat > morph-sk.txt

dictionaries: dictionaries/sk_SK-spell/sk_SK.dic dictionaries/sk_SK-lemma-ascii/sk_SK.dic dictionaries/sk_SK-lemma/sk_SK.dic

dictionaries/sk_SK-spell/sk_SK.dic: morph-sk.txt target/hunspell-builder-0.2-jar-with-dependencies.jar
	mkdir -p dictionaries/sk_SK-spell
	java -jar target/hunspell-builder-0.2-jar-with-dependencies.jar morph-sk.txt dictionaries/sk_SK-spell/sk_SK

dictionaries/sk_SK-lemma-ascii/sk_SK.dic: morph-sk.txt target/hunspell-builder-0.2-jar-with-dependencies.jar
	mkdir -p dictionaries/sk_SK-lemma-ascii
	java -jar target/hunspell-builder-0.2-jar-with-dependencies.jar morph-sk.txt dictionaries/sk_SK-lemma-ascii/sk_SK --ascii --for-lemmatizer

dictionaries/sk_SK-lemma/sk_SK.dic: morph-sk.txt target/hunspell-builder-0.2-jar-with-dependencies.jar
	mkdir -p dictionaries/sk_SK-lemma
	java -jar target/hunspell-builder-0.2-jar-with-dependencies.jar morph-sk.txt dictionaries/sk_SK-lemma/sk_SK --for-lemmatizer

# This does not work yet
# merged dictionary (you probably cannot distribute this file due to license)
#dictionaries/sk_SK-merged-spell: merge-words.txt

#merge-words.txt: hunspell-sk-20110228/sk_SK.dic dictionaries/sk_SK-spell/sk_SK.dic
#	unmunch hunspell-sk-20110228/sk_SK.dic hunspell-sk-20110228/sk_SK.aff | hunspell -d dictionaries/sk_SK-spell/sk_SK -i utf-8 | grep -e '^& ' | sed -e 's/^& \([^ ]*\) .*/\1/' > merge-words.txt

#hunspell-sk-20110228/sk_SK.dic:
#	wget 'http://www.sk-spell.sk.cx/files/hunspell-sk-20110228.zip'
#	unzip hunspell-sk-20110228.zip
	
