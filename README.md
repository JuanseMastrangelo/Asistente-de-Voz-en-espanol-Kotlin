echo "Last version:" `version ./`

# increase the version and keep it in the VERSION variable
VERSION=`version -n ./ +`

echo "New version: $VERSION"

# get the human-readable version number
SHORTVERSION=`version -short -n ./`

echo "Tagging release..."

# Tag the new release
git tag -a `echo $VERSION` -m "Release SHORTVERSION"

echo "Updating version file..."

# Commit the new .version file, since it's changed
git commit .version -m "Updated to version $SHORTVERSION"

echo "Pushing changes..."

# push changes and tags
git push origin master
git push --tags

echo "Finished"
