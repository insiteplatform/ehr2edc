{{/* vim: set filetype=mustache: */}}
{{/*
Expand the name of the chart.
*/}}
{{- define "ehr2edc-service.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
*/}}
{{- define "ehr2edc-service.fullname" -}}
{{- $name := default .Chart.Name .Values.nameOverride -}}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Get a hostname from URL
*/}}
{{- define "hostname" -}}
{{- . | trimPrefix "http://" |  trimPrefix "https://" | trimSuffix "/" | quote -}}
{{- end -}}

{{/*
Memory related resources
Calculate proper Xmx value for resource constraint specified
*/}}
{{- define "memoryToXmx" -}}
{{- printf "-Xmx%dm" (div (mul .Values.definedResources.memory 75) 100) | trim -}}
{{- end -}}

{{/*
Memory related resources
*/}}
{{- define "memoryRequest" -}}
{{- printf "%dMi" (div (mul .Values.definedResources.memory 75) 100) | trim -}}
{{- end -}}

{{/*
Memory related resources
*/}}
{{- define "memoryLimit" -}}
{{- printf "%6.0fMi" .Values.definedResources.memory | trim -}}
{{- end -}}

{{/*
Cpu related resources
*/}}
{{- define "cpuRequest" -}}
{{- printf "%6.0fm" .Values.definedResources.cpu | trim -}}
{{- end -}}

{{/*
Cpu related resources
*/}}
{{- define "cpuLimit" -}}
{{- printf "%6.0fm" .Values.definedResources.cpu | trim -}}
{{- end -}}

