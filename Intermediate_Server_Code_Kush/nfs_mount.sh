#!/bin/bash

# Check if nfs-common package installed
check_installed=`dpkg -s nfs-common`

if ! [[ $check_installed = *"installed"* ]]; then
  sudo apt update
  sudo apt install -y nfs-common
fi


nfs_key='nfs-share'
if grep $nfs_key /etc/fstab > /dev/null
then
	echo "**NFS Server Already Configured**"
else
	echo "Enter Ip of NFS server:"
	read nfs_Ip

	# Adding the command to fstab file
	connect_to_nfs_command=":/home/pmadhukar/nfs_share_folder /mnt/nfs-share nfs rw,soft,intr,noatime,x-gvfs-show"
	add_nfs=$nfs_Ip$connect_to_nfs_command
	echo $add_nfs >> /etc/fstab
	
	# Create mounting folder if not already present
	if [ ! -d "/mnt/nfs-share" ]; then
		mkdir "/mnt/nfs-share"
	fi

	# Command to mount the unmounted file systems
	mount -a
fi

