SLUG="sockeqwe/mosby"
JDK="oraclejdk8"
BRANCH="master"

set -e

if [ "$TRAVIS_REPO_SLUG" != "$SLUG" ]; then
  echo "Skipping snapshot deployment: wrong repository. Expected '$SLUG' but was '$TRAVIS_REPO_SLUG'."
elif [ "$TRAVIS_JDK_VERSION" != "$JDK" ]; then
  echo "Skipping snapshot deployment: wrong JDK. Expected '$JDK' but was '$TRAVIS_JDK_VERSION'."
elif [ "$TRAVIS_PULL_REQUEST" != "false" ]; then
  echo "Skipping snapshot deployment: was pull request."
elif [ "$TRAVIS_BRANCH" != "$BRANCH" ]; then
  echo "Skipping snapshot deployment: wrong branch. Expected '$BRANCH' but was '$TRAVIS_BRANCH'."
else
  echo "Deploying..."
  ./gradlew --stop
  echo "org.gradle.parallel=false" >> ~/.gradle/gradle.properties
  echo "org.gradle.configureondemand=false" >> ~/.gradle/gradle.properties
  openssl aes-256-cbc -K $encrypted_8739fbca6d38_key -iv $encrypted_8739fbca6d38_iv -in .buildscript/key.gpg.enc -out key.gpg -d
  gpg --import key.gpg
  echo "signing.keyId=E508C045" >> gradle.properties
  echo "signing.password=$PGP_KEY" >> gradle.properties
  echo "signing.secretKeyRingFile=/home/travis/.gnupg/secring.gpg" >> gradle.properties
  echo "org.gradle.parallel=false" >> gradle.properties
  echo "org.gradle.configureondemand=false" >> gradle.properties
  ./gradlew --no-daemon uploadArchives -Dorg.gradle.parallel=false -Dorg.gradle.configureondemand=false
  rm key.gpg
  git reset --hard
  echo "Deployed!"
fi
