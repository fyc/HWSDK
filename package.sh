#!/usr/bin/env bash
rm -r output
mkdir output



#cp -rf app third-party-libs app/libs 接入说明/*.html 接入说明/*.txt -t output
#cd output
#rm -r app/build
#zip -r demo.zip  app
#rm -r app
#cd ..
#zip -r ttsdk.zip output



cp app/libs/yiyou-*.jar output

cp output-libs/release/merge/assets/apk/*.apk output/
cd output
zip  update.zip  *.apk
md5sum update.zip > md5.txt
rm *.apk




