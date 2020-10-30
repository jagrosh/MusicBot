#!/bin/bash
usage(){ printf " -h prints this message \n -v <version_number> what version you wish to build \n -l ie latest. you wish to push this to docker tag :latest \n -u <username> dockerhub username \n -r <reponame> the repository you wish to push this to. \n example: ${0} -v 0.3.1 -u jagrosh -r jmusicbot -l \n" ; exit ;}

while getopts "v:u:r:lh" o; do
    case "${o}" in
        h)
            usage
            ;;
        v)
            VALID="^[0-9.]+$"
            VERSION=${OPTARG}
            [ -z ${VERSION} ] && { echo "-v required:" ; usage ;}
            [[ ${VERSION} =~ ${VALID} ]] || { echo "-v invalid input e.g: 0.3.1" ; usage ;}
            ;;
        u)
            USERNAME=${OPTARG}
            [ -z ${USERNAME} ] && { echo "-u required:" ; usage ;}
            ;;
        r) 
            REPONAME=${OPTARG}
            [ -z ${USERNAME} ] && { echo "-r required:" ; usage ;}
            ;;
        l)
            LATEST=1
            ;;
        *)
            usage
            exit 1
            ;;
    esac
done


dockbuild(){
    [ -z ${LATEST} ] && docker build -t "${USERNAME}/${REPONAME}:${VERSION}" .
    [ -z ${LATEST} ] || docker build -t "${USERNAME}/${REPONAME}:${VERSION}" -t ${USERNAME}/${REPONAME}:latest .
}

dockpush(){
    [ -z ${LATEST} ] && docker push "${USERNAME}/${REPONAME}:${VERSION}"
    [ -z ${LATEST} ] || docker push "${USERNAME}/${REPONAME}:${VERSION}" ; docker push "${USERNAME}/${REPONAME}:latest"
}

innit(){
    [ -z ${LATEST} ] || sed -i "s/^ARG VERSION=.*/ARG VERSION=\"${VERSION}\"/g" Dockerfile
    dockbuild
    dockpush
    [ -z ${LATEST} ] || sed -i "s/^ARG VERSION=.*/ARG VERSION=\"\"/g" Dockerfile
    exit 0
}

innit