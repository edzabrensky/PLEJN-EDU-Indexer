# EDU Crawler
##### Team Members: Ed Zabrensky, Lisa Chen, Nikhil Gowda, Jason Zellmer, Poorvaja Sundar

## Description

Our project creates a edu page web crawler using jsoup and an indexer with lucene.This crawler will crawl edu websites from an initial seed of edu websites. The user specifies the initial seed of edu websites, the amount of data of data to crawl and the output directory. 

### Installing

The following packages are needed for the crawler to work.

```
jsoup 1.8.3 or higher
json-20140107 or higher
oracle jdk8
Lucene core
```



## Deployment

This is how we run the Crawler. 

```
./crawler.sh <seed of .edu addresses file> <Amount of data to collect in Megabytes> <out-put-dir>
(Note that the out-put-dir should be created beforehand)
```

Example:
```
./crawler.sh listOfUniversities.txt 1 data 
```


