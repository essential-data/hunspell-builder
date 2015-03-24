hunspell-builder
=========================================

Copyright
---------

Copyright (c) 2015 Essential Data, s.r.o.

This code can be used according to Apache License, verzia 2.0 from january 2004

More information in LICENSE. 

Do you like working with natural language processing and coding? Work for us!
-----------------------------------------------------------------------------

Essential Data works with language, with big data and on interesting projects. Check out 
[list of open job positions](http://www.essential-data.sk/pracujte-pre-nas/) and work in an amazing
team!

About this project
------------------

We built this project to create [hunspell dictionaries for Slovak language](https://github.com/essential-data/hunspell-sk).
We got list of words in the form of

```
lemma form1 tag1
lemma form2 tag2
```

``tag`` is a morphological tag, which is not used in this project (and can be any string). ``lemma`` and ``form`` contain various
word forms for a particular word (``lemma`` being the base form). Of course we could build a hunspell dictionary by simply listing all words, this has
several disadvantages though: First, this dictionary would be unnecessarily large (and slow in naive hunspell implementations).
The second problem is that we want to [use this dictionary with Elastic search](https://github.com/essential-data/elasticsearch-sk).
This means we need to keep the relation between base form (lemma) and all other forms.

This project builds hunspell files from this input file.

Usage
-----

You can compile the java source with 

```
mvn package
```

If you want to create Slovak dictionaries (which automatically downloads the right dictionary file), you can run

```
make
```

*Warning!* The [resulting files](https://github.com/essential-data/hunspell-sk) are
[under a different license](https://github.com/essential-data/hunspell-sk/blob/master/LICENSE.txt)
based on license of the input data!

This results in three dictionaries: two for lemmatization (ascii and full UTF-8 version) and one for spell checking.
The version for spell-checking does not keep the relation between lemma and all its various forms. We need this dictionary
due to a bug in many hunspell implementations (like LibreOffice), which does not allow PREFIX or SUFFIX rule to change
the whole word (which can happen, for example in slovak language, ``byť`` is the lemma of ``sú``). More information
about Slovak language application of this project is in 
[hunspell-sk project](https://github.com/essential-data/hunspell-sk).

Links
-----

* [Github of Essential Data](https://github.com/essential-data/) - our open-source projects
* [Open job listings](http://www.essential-data.sk/pracujte-pre-nas/) in Essential Data
* [hunspell-sk project](https://github.com/essential-data/hunspell-sk).