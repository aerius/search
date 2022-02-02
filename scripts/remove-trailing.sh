cd ..
find . -name "*.java" | xargs sed -i 's/[ \t]*$//'
find . -name "*.html" | xargs sed -i 's/[ \t]*$//'
find . -name "*.*css" | xargs sed -i 's/[ \t]*$//'
find . -name "*.*xml" | xargs sed -i 's/[ \t]*$//'
