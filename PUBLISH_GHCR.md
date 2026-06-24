# Publishing Docker Images to GHCR

The release workflow automatically builds and pushes Docker images to GitHub Container Registry (GHCR). This guide explains how to set it up and trigger a release.

## Prerequisites

- A GitHub repository with write access
- The `GITHUB_TOKEN` secret (automatically provided by GitHub Actions)

No additional secrets are needed — GitHub Actions provides `GITHUB_TOKEN` with sufficient permissions to push to GHCR within the same repository.

## Triggering a Release

1. Go to your repository on GitHub
2. Click **Actions** → **Make Release** → **Run workflow**
3. Fill in:
   - **Version Number** — e.g. `0.4.1` (must be a valid semver tag)
   - **Description** — release notes (Markdown supported)
4. Click **Run workflow**

The workflow will:

1. Build the JAR
2. Publish it as a release artifact
3. Build the Docker image from `Dockerfile`
4. Push two tags to GHCR:
   - `ghcr.io/<owner>/<repo>:<version>` — e.g. `ghcr.io/lukas48452/musicbot:0.4.1`
   - `ghcr.io/<owner>/<repo>:latest`

## Pulling the Image

```sh
docker pull ghcr.io/<owner>/<repo>:latest
```

Replace `<owner>/<repo>` with your GitHub username/org and repository name.

## Example

```sh
# Pull the latest release
docker pull ghcr.io/lukas48452/musicbot:latest

# Run with a config volume
docker run -v ./config:/app/config ghcr.io/lukas48452/musicbot:latest
```

## Local Testing

To test the Docker build locally before publishing:

```sh
docker compose build
docker compose up -d
docker compose logs -f
```

## Manual Push (Alternative)

If you want to push manually (without the workflow):

```sh
# Build the image
docker build -t ghcr.io/<owner>/<repo>:<version> .

# Log in to GHCR
echo "$GITHUB_TOKEN" | docker login ghcr.io -u <username> --password-stdin

# Push
docker push ghcr.io/<owner>/<repo>:<version>
docker push ghcr.io/<owner>/<repo>:latest
```
