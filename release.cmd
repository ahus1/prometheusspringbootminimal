rem create a tag, push it, and then TraviCI will build the new artifact and attach it to that GitHub release
git checkout master
git tag -f latest
git push -f origin latest

rem to clean a tag remotely
rem git push --delete origin latest
