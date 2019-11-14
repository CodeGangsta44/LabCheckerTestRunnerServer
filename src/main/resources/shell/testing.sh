#!/bin/bash
# git clone git@github.com:CodeGangsta44/labCheckerTestTemplateRepoTest.git;
# git clone git@github.com:CodeGangsta44/labCheckerTestTemplateRepo.git;

#usage example: ./testing.sh git@github.com:CodeGangsta44/labCheckerTestTemplateRepoTest.git git@github.com:CodeGangsta44/labCheckerTestTemplateRepo.git

#output example:
    # Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.075 sec
    # Tests run: 4, Failures: 0, Errors: 0, Skipped: 0

mkdir -p testing-area/$4/stage;
exec > testing-area/$4/log.txt;
cd testing-area/$4/stage;
git clone $1 2>&1;
git clone $2 -b "variant_"$3 2>&1;
mkdir -p labCheckerTestTemplateRepoTest/src/main/java;
cp -R labCheckerTestTemplateRepo/src/main/java/ labCheckerTestTemplateRepoTest/src/main/;
cd labCheckerTestTemplateRepoTest;

mvn test 2>&1 | grep '^Tests run:' > ../../results.txt;

cd ../..;
rm -rf stage;
